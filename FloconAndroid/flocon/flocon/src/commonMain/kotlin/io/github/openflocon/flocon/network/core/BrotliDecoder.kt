package io.github.openflocon.flocon.network.core

import io.github.openflocon.flocon.InternalFloconApi

/** Shown instead of a Brotli body that could not be decoded (no decoder on iOS and Wasm). */
@InternalFloconApi
const val BROTLI_BODY_PLACEHOLDER: String = "[Brotli-encoded body]"

/**
 * Decodes a Brotli (`Content-Encoding: br`) body for the network interceptors.
 * Returns null when the platform has no decoder or when the bytes are not valid Brotli.
 */
@InternalFloconApi
expect fun decodeBrotli(bytes: ByteArray): ByteArray?
