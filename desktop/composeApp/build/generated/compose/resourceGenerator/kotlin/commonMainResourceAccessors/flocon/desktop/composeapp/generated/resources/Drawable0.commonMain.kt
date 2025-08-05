@file:OptIn(InternalResourceApi::class)

package flocon.desktop.composeapp.generated.resources

import kotlin.OptIn
import kotlin.String
import kotlin.collections.MutableMap
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.InternalResourceApi
import org.jetbrains.compose.resources.ResourceItem

private const val MD: String = "composeResources/flocon.desktop.composeapp.generated.resources/"

internal val Res.drawable.app_icon: DrawableResource by lazy {
      DrawableResource("drawable:app_icon", setOf(
        ResourceItem(setOf(), "${MD}drawable/app_icon.png", -1, -1),
      ))
    }

internal val Res.drawable.app_icon_small: DrawableResource by lazy {
      DrawableResource("drawable:app_icon_small", setOf(
        ResourceItem(setOf(), "${MD}drawable/app_icon_small.png", -1, -1),
      ))
    }

internal val Res.drawable.compose_multiplatform: DrawableResource by lazy {
      DrawableResource("drawable:compose_multiplatform", setOf(
        ResourceItem(setOf(), "${MD}drawable/compose-multiplatform.xml", -1, -1),
      ))
    }

internal val Res.drawable.graphql: DrawableResource by lazy {
      DrawableResource("drawable:graphql", setOf(
        ResourceItem(setOf(), "${MD}drawable/graphql.png", -1, -1),
      ))
    }

internal val Res.drawable.grpc: DrawableResource by lazy {
      DrawableResource("drawable:grpc", setOf(
        ResourceItem(setOf(), "${MD}drawable/grpc.png", -1, -1),
      ))
    }

internal val Res.drawable.smartphone: DrawableResource by lazy {
      DrawableResource("drawable:smartphone", setOf(
        ResourceItem(setOf(), "${MD}drawable/smartphone.png", -1, -1),
      ))
    }

@InternalResourceApi
internal fun _collectCommonMainDrawable0Resources(map: MutableMap<String, DrawableResource>) {
  map.put("app_icon", Res.drawable.app_icon)
  map.put("app_icon_small", Res.drawable.app_icon_small)
  map.put("compose_multiplatform", Res.drawable.compose_multiplatform)
  map.put("graphql", Res.drawable.graphql)
  map.put("grpc", Res.drawable.grpc)
  map.put("smartphone", Res.drawable.smartphone)
}
