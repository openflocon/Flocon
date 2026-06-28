package io.github.openflocon.flocon.myapplication.multi.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import io.github.openflocon.flocon.myapplication.multi.AndroidTestContext
import io.github.openflocon.flocon.myapplication.multi.Databases
import io.github.openflocon.flocon.myapplication.multi.database.model.DogEntity
import io.github.openflocon.flocon.myapplication.multi.grpc.GrpcController
import kotlinx.coroutines.launch
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
@Composable
actual fun PlatformSpecificTests(modifier: Modifier) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    FlowRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Button(
            onClick = {
                AndroidTestContext.dummyHttpCaller.call()
            }
        ) {
            Text("okhttp test")
        }

        Button(
            onClick = {
                AndroidTestContext.dummyHttpCaller.callGzip()
            }
        ) {
            Text("okhttp gzip test")
        }

        Button(
            onClick = {
                scope.launch {
                    AndroidTestContext.graphQlTester.fetchViewerInfo()
                }
            }
        ) {
            Text("graphql test")
        }

        Button(
            onClick = {
                scope.launch {
                    GrpcController.sayHello()
                }
            }
        ) {
            Text("grpc test")
        }

        Button(
            onClick = {
                AndroidTestContext.dummyWebsocketCaller.send(Uuid.random().toString())
            }
        ) {
            Text("websocket test")
        }

        Button(
            onClick = {
                throw Throwable("my custom crash")
            }
        ) {
            Text("crash")
        }

        Button(
            onClick = {
                scope.launch {
                    Databases.getDogDatabase(context).dogDao().insertDog(
                        DogEntity(
                            id = System.currentTimeMillis(),
                            name = "Flocon",
                            breed = "Golden Retriever ${System.currentTimeMillis()}",
                            age = 6,
                            pictureUrl = "https://picsum.photos/501/500.jpg",
                        )
                    )
                }
            }
        ) {
            Text("Insert dog in DB")
        }

        Button(
            onClick = {
                scope.launch {
                    AndroidTestContext.inMemoryDb.dogDao().insertDog(
                        DogEntity(
                            id = System.currentTimeMillis(),
                            name = "InMemory Flocon",
                            breed = "Golden Retriever ${System.currentTimeMillis()}",
                            age = 6,
                            pictureUrl = "https://picsum.photos/501/500.jpg",
                        )
                    )
                }
            }
        ) {
            Text("Insert dog in InMemory DB")
        }
    }
}
