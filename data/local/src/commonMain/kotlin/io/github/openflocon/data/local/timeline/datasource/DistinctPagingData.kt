package io.github.openflocon.data.local.timeline.datasource

import androidx.paging.PagingData
import androidx.paging.filter

/**
 * Keeps the first item of each [key]. The pages of a limit / offset source are loaded one after the other: a row inserted between
 * two loads shifts the offsets, and the next page can hold items the previous one already returned.
 * The set lives as long as this [PagingData]: a new generation (after an invalidation) starts with an empty one.
 */
internal fun <T : Any> PagingData<T>.distinctBy(key: (T) -> Any): PagingData<T> {
    val seen = HashSet<Any>()
    return filter { seen.add(key(it)) }
}
