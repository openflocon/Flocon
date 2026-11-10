package io.github.openflocon.flocon

import android.content.Context

fun startFlocon(
    context: Context,
    block: FloconConfiguration.() -> Unit
) = startFlocon(FloconContext(context), block)
