package io.github.openflocon.flocon.myapplication

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

// 1. Modèle de données pour une photo en vol
data class FlyingPhoto(
    val id: Long,
    val resId: Int, // R.drawable.xxx
    val angle: Double, // Angle de trajectoire
    val rotation: Float, // Rotation légère de l'image elle-même
    val speed: Long // Durée de l'animation pour cette image
)

@Composable
fun WarpSpeedEffect(
    photos: List<Int> // Liste de tes ressources R.drawable.xxx
) {
    // État pour stocker les photos actuellement à l'écran
    val activePhotos = remember { mutableStateListOf<FlyingPhoto>() }
    
    // Compteur pour générer des IDs uniques
    var photoCounter by remember { mutableLongStateOf(0L) }

    // Boucle d'apparition (Spawning Loop)
    LaunchedEffect(Unit) {
        while (isActive) {
            // Ajouter une nouvelle photo toutes les X millisecondes
            delay(Random.nextLong(100, 300)) 
            
            if (photos.isNotEmpty()) {
                val newPhoto = FlyingPhoto(
                    id = photoCounter++,
                    resId = photos.random(),
                    angle = Random.nextDouble(0.0, 360.0),
                    rotation = Random.nextFloat() * 30f - 15f, // Tilt entre -15 et 15 degrés
                    speed = Random.nextLong(1500, 2500)
                )
                activePhotos.add(newPhoto)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        // Optionnel : Fond étoilé (Speed lines) pour accentuer la vitesse
        StarField()

        // Rendu des photos
        activePhotos.forEach { photo ->
            FlyingPhotoItem(
                photo = photo,
                onAnimationFinished = {
                    activePhotos.remove(photo)
                }
            )
        }
    }
}

@Composable
fun FlyingPhotoItem(
    photo: FlyingPhoto,
    onAnimationFinished: () -> Unit
) {
    // Animation de 0f à 1f
    val animatable = remember { Animatable(0f) }
    
    // Dimensions de l'écran pour calculer la sortie
    val config = LocalConfiguration.current
    val density = LocalDensity.current
    val screenWidthPx = with(density) { config.screenWidthDp.dp.toPx() }
    val maxDistance = screenWidthPx * 1.5f // Distance pour sortir de l'écran

    LaunchedEffect(photo) {
        // Lancer l'animation avec une courbe exponentielle pour l'effet "Warp"
        animatable.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = photo.speed.toInt(),
                easing = CubicBezierEasing(0.5f, 0f, 1f, 0.5f) // Accélération vers la fin
            )
        )
        onAnimationFinished()
    }

    val progress = animatable.value
    
    // Calcul de la position : On part du centre (0) vers l'extérieur
    // La distance augmente exponentiellement avec le progrès
    val currentDistance = maxDistance * progress
    val xOffset = (currentDistance * cos(Math.toRadians(photo.angle))).toFloat()
    val yOffset = (currentDistance * sin(Math.toRadians(photo.angle))).toFloat()
    
    // Scale : commence petit, finit grand
    val scale = 0.1f + (progress * 2.5f)

    // Alpha : Fade in rapide, reste opaque
    val alpha = if (progress < 0.1f) progress * 10f else 1f

    Image(
        painter = painterResource(id = photo.resId),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .size(120.dp) // Taille de base de l'image
            .graphicsLayer {
                translationX = xOffset
                translationY = yOffset
                scaleX = scale
                scaleY = scale
                rotationZ = photo.rotation
                this.alpha = alpha
            }
    )
}

// Petit bonus : L'effet de lignes blanches en arrière-plan
@Composable
fun StarField() {
    val infiniteTransition = rememberInfiniteTransition(label = "stars")
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing)
        ), label = "timer"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val centerX = size.width / 2
        val centerY = size.height / 2
        
        // Dessiner 50 "étoiles" ou lignes de vitesse
        for (i in 0..50) {
            // Pseudo-aléatoire stable basé sur l'index
            val angle = (i * 137.5) % 360 
            val rad = Math.toRadians(angle)
            val speedFactor = (i % 5 + 1) * 0.5f
            
            // Simuler le mouvement
            val currentProgress = (time * speedFactor + (i * 0.1f)) % 1f
            
            // Les étoiles proches du centre sont courtes, celles au bord sont longues (motion blur)
            val startDist = currentProgress * size.width
            val length = 20f + (currentProgress * 200f) // Plus long quand c'est loin
            
            val startX = centerX + (startDist * cos(rad)).toFloat()
            val startY = centerY + (startDist * sin(rad)).toFloat()
            
            val endX = centerX + ((startDist + length) * cos(rad)).toFloat()
            val endY = centerY + ((startDist + length) * sin(rad)).toFloat()
            
            drawLine(
                color = Color.White.copy(alpha = currentProgress), // Fade in/out
                start = Offset(startX, startY),
                end = Offset(endX, endY),
                strokeWidth = 2f * currentProgress
            )
        }
    }
}