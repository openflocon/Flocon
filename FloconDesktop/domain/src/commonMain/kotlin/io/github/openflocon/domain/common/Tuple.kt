package io.github.openflocon.domain.common

data class Tuple1<A>(
    val first: A,
) : java.io.Serializable

fun <A> tupleOf(first: A) = Tuple1(
    first = first,
)

fun <A, B> tupleOf(first: A, second: B) = Pair(
    first = first,
    second = second,
)

fun <A, B, C> tupleOf(first: A, second: B, third: C) = Triple(
    first = first,
    second = second,
    third = third,
)

data class Tuple4<A, B, C, D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D,
) : java.io.Serializable

fun <A, B, C, D> tupleOf(first: A, second: B, third: C, fourth: D) = Tuple4(
    first = first,
    second = second,
    third = third,
    fourth = fourth,
)

data class Tuple5<A, B, C, D, E>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D,
    val fifth: E,
) : java.io.Serializable

fun <A, B, C, D, E> tupleOf(first: A, second: B, third: C, fourth: D, fifth: E) = Tuple5(
    first = first,
    second = second,
    third = third,
    fourth = fourth,
    fifth = fifth,
)

data class Tuple6<A, B, C, D, E, F>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D,
    val fifth: E,
    val sixth: F,
) : java.io.Serializable

fun <A, B, C, D, E, F> tupleOf(first: A, second: B, third: C, fourth: D, fifth: E, sixth: F) = Tuple6(
    first = first,
    second = second,
    third = third,
    fourth = fourth,
    fifth = fifth,
    sixth = sixth,
)

data class Tuple7<A, B, C, D, E, F, G>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D,
    val fifth: E,
    val sixth: F,
    val seventh: G,
) : java.io.Serializable

data class Tuple8<A, B, C, D, E, F, G, H>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D,
    val fifth: E,
    val sixth: F,
    val seventh: G,
    val eighth: H,
) : java.io.Serializable

data class Tuple9<A, B, C, D, E, F, G, H, I>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D,
    val fifth: E,
    val sixth: F,
    val seventh: G,
    val eighth: H,
    val ninth: I,
) : java.io.Serializable

data class Tuple10<A, B, C, D, E, F, G, H, I, J>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D,
    val fifth: E,
    val sixth: F,
    val seventh: G,
    val eighth: H,
    val ninth: I,
    val tenth: J,
) : java.io.Serializable

data class Tuple11<A, B, C, D, E, F, G, H, I, J, K>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D,
    val fifth: E,
    val sixth: F,
    val seventh: G,
    val eighth: H,
    val ninth: I,
    val tenth: J,
    val eleventh: K,
) : java.io.Serializable

data class Tuple12<A, B, C, D, E, F, G, H, I, J, K, L>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D,
    val fifth: E,
    val sixth: F,
    val seventh: G,
    val eighth: H,
    val ninth: I,
    val tenth: J,
    val eleventh: K,
    val twelfth: L,
) : java.io.Serializable

data class Tuple13<A, B, C, D, E, F, G, H, I, J, K, L, M>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D,
    val fifth: E,
    val sixth: F,
    val seventh: G,
    val eighth: H,
    val ninth: I,
    val tenth: J,
    val eleventh: K,
    val twelfth: L,
    val thirteenth: M,
) : java.io.Serializable

data class Tuple14<A, B, C, D, E, F, G, H, I, J, K, L, M, N>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D,
    val fifth: E,
    val sixth: F,
    val seventh: G,
    val eighth: H,
    val ninth: I,
    val tenth: J,
    val eleventh: K,
    val twelfth: L,
    val thirteenth: M,
    val fourteenth: N,
) : java.io.Serializable

data class Tuple15<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D,
    val fifth: E,
    val sixth: F,
    val seventh: G,
    val eighth: H,
    val ninth: I,
    val tenth: J,
    val eleventh: K,
    val twelfth: L,
    val thirteenth: M,
    val fourteenth: N,
    val fifteenth: O,
) : java.io.Serializable

data class Tuple16<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D,
    val fifth: E,
    val sixth: F,
    val seventh: G,
    val eighth: H,
    val ninth: I,
    val tenth: J,
    val eleventh: K,
    val twelfth: L,
    val thirteenth: M,
    val fourteenth: N,
    val fifteenth: O,
    val sixteenth: P,
) : java.io.Serializable

data class Tuple17<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D,
    val fifth: E,
    val sixth: F,
    val seventh: G,
    val eighth: H,
    val ninth: I,
    val tenth: J,
    val eleventh: K,
    val twelfth: L,
    val thirteenth: M,
    val fourteenth: N,
    val fifteenth: O,
    val sixteenth: P,
    val seventeenth: Q,
) : java.io.Serializable

data class Tuple18<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D,
    val fifth: E,
    val sixth: F,
    val seventh: G,
    val eighth: H,
    val ninth: I,
    val tenth: J,
    val eleventh: K,
    val twelfth: L,
    val thirteenth: M,
    val fourteenth: N,
    val fifteenth: O,
    val sixteenth: P,
    val seventeenth: Q,
    val eighteenth: R,
) : java.io.Serializable

data class Tuple19<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D,
    val fifth: E,
    val sixth: F,
    val seventh: G,
    val eighth: H,
    val ninth: I,
    val tenth: J,
    val eleventh: K,
    val twelfth: L,
    val thirteenth: M,
    val fourteenth: N,
    val fifteenth: O,
    val sixteenth: P,
    val seventeenth: Q,
    val eighteenth: R,
    val nineteenth: S,
) : java.io.Serializable

data class Tuple20<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D,
    val fifth: E,
    val sixth: F,
    val seventh: G,
    val eighth: H,
    val ninth: I,
    val tenth: J,
    val eleventh: K,
    val twelfth: L,
    val thirteenth: M,
    val fourteenth: N,
    val fifteenth: O,
    val sixteenth: P,
    val seventeenth: Q,
    val eighteenth: R,
    val nineteenth: S,
    val twentieth: T,
) : java.io.Serializable
