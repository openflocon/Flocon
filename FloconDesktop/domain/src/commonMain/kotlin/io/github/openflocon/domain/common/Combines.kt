package io.github.openflocon.domain.common

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

fun <T1> combines(flow1: Flow<T1>): Flow<Tuple1<T1>> =
    flow1.map(::Tuple1)

fun <T1, T2> Flow<T1>.combine(flow: Flow<T2>): Flow<Pair<T1, T2>> = this.combine(flow, ::Pair)

fun <T1, T2> combines(flow1: Flow<T1>, flow2: Flow<T2>): Flow<Pair<T1, T2>> =
    combine(flow1, flow2, ::Pair)

fun <T1, T2, T3> Flow<T1>.combine(
    flow2: Flow<T2>,
    flow3: Flow<T3>,
): Flow<Triple<T1, T2, T3>> = combine(this, flow2, flow3, ::Triple)

fun <T1, T2, T3> combines(
    flow1: Flow<T1>,
    flow2: Flow<T2>,
    flow3: Flow<T3>,
): Flow<Triple<T1, T2, T3>> = combine(flow1, flow2, flow3, ::Triple)

fun <T1, T2, T3, T4> Flow<T1>.combine(
    flow2: Flow<T2>,
    flow3: Flow<T3>,
    flow4: Flow<T4>,
): Flow<Tuple4<T1, T2, T3, T4>> = combine(this, flow2, flow3, flow4, ::Tuple4)

fun <T1, T2, T3, T4> combines(
    flow1: Flow<T1>,
    flow2: Flow<T2>,
    flow3: Flow<T3>,
    flow4: Flow<T4>,
): Flow<Tuple4<T1, T2, T3, T4>> = combine(flow1, flow2, flow3, flow4, ::Tuple4)

fun <T1, T2, T3, T4, T5> Flow<T1>.combine(
    flow2: Flow<T2>,
    flow3: Flow<T3>,
    flow4: Flow<T4>,
    flow5: Flow<T5>,
): Flow<Tuple5<T1, T2, T3, T4, T5>> = combine(this, flow2, flow3, flow4, flow5, ::Tuple5)

fun <T1, T2, T3, T4, T5> combines(
    flow1: Flow<T1>,
    flow2: Flow<T2>,
    flow3: Flow<T3>,
    flow4: Flow<T4>,
    flow5: Flow<T5>,
): Flow<Tuple5<T1, T2, T3, T4, T5>> = combine(flow1, flow2, flow3, flow4, flow5, ::Tuple5)

fun <T1, T2, T3, T4, T5, T6> combines(
    flow1: Flow<T1>,
    flow2: Flow<T2>,
    flow3: Flow<T3>,
    flow4: Flow<T4>,
    flow5: Flow<T5>,
    flow6: Flow<T6>,
): Flow<Tuple6<T1, T2, T3, T4, T5, T6>> = combine(flow1, flow2, flow3, flow4, flow5, flow6) {
    Tuple6(
        it[0] as T1,
        it[1] as T2,
        it[2] as T3,
        it[3] as T4,
        it[4] as T5,
        it[5] as T6,
    )
}

fun <T1, T2, T3, T4, T5, T6, R> combines(
    flow: Flow<T1>,
    flow2: Flow<T2>,
    flow3: Flow<T3>,
    flow4: Flow<T4>,
    flow5: Flow<T5>,
    flow6: Flow<T6>,
    transform: suspend (T1, T2, T3, T4, T5, T6) -> R,
): Flow<R> = combine(flow, flow2, flow3, flow4, flow5, flow6) { args: Array<*> ->
    transform(
        args[0] as T1,
        args[1] as T2,
        args[2] as T3,
        args[3] as T4,
        args[4] as T5,
        args[5] as T6,
    )
}

fun <T1, T2, T3, T4, T5, T6, T7> combines(
    flow1: Flow<T1>,
    flow2: Flow<T2>,
    flow3: Flow<T3>,
    flow4: Flow<T4>,
    flow5: Flow<T5>,
    flow6: Flow<T6>,
    flow7: Flow<T7>,
): Flow<Tuple7<T1, T2, T3, T4, T5, T6, T7>> = combine(flow1, flow2, flow3, flow4, flow5, flow6, flow7) {
    Tuple7(
        it[0] as T1,
        it[1] as T2,
        it[2] as T3,
        it[3] as T4,
        it[4] as T5,
        it[5] as T6,
        it[6] as T7,
    )
}

fun <T1, T2, T3, T4, T5, T6, T7, R> combines(
    flow: Flow<T1>,
    flow2: Flow<T2>,
    flow3: Flow<T3>,
    flow4: Flow<T4>,
    flow5: Flow<T5>,
    flow6: Flow<T6>,
    flow7: Flow<T7>,
    transform: suspend (T1, T2, T3, T4, T5, T6, T7) -> R,
): Flow<R> = combine(flow, flow2, flow3, flow4, flow5, flow6, flow7) { args: Array<*> ->
    transform(
        args[0] as T1,
        args[1] as T2,
        args[2] as T3,
        args[3] as T4,
        args[4] as T5,
        args[5] as T6,
        args[6] as T7,
    )
}

fun <T1, T2, T3, T4, T5, T6, T7, T8> combines(
    flow1: Flow<T1>,
    flow2: Flow<T2>,
    flow3: Flow<T3>,
    flow4: Flow<T4>,
    flow5: Flow<T5>,
    flow6: Flow<T6>,
    flow7: Flow<T7>,
    flow8: Flow<T8>,
): Flow<Tuple8<T1, T2, T3, T4, T5, T6, T7, T8>> = combine(flow1, flow2, flow3, flow4, flow5, flow6, flow7, flow8) {
    Tuple8(
        it[0] as T1,
        it[1] as T2,
        it[2] as T3,
        it[3] as T4,
        it[4] as T5,
        it[5] as T6,
        it[6] as T7,
        it[7] as T8,
    )
}

fun <T1, T2, T3, T4, T5, T6, T7, T8, T9> combines(
    flow1: Flow<T1>,
    flow2: Flow<T2>,
    flow3: Flow<T3>,
    flow4: Flow<T4>,
    flow5: Flow<T5>,
    flow6: Flow<T6>,
    flow7: Flow<T7>,
    flow8: Flow<T8>,
    flow9: Flow<T9>,
): Flow<Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9>> =
    combine(flow1, flow2, flow3, flow4, flow5, flow6, flow7, flow8, flow9) {
        Tuple9(
            it[0] as T1,
            it[1] as T2,
            it[2] as T3,
            it[3] as T4,
            it[4] as T5,
            it[5] as T6,
            it[6] as T7,
            it[7] as T8,
            it[8] as T9,
        )
    }

fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> combines(
    flow1: Flow<T1>,
    flow2: Flow<T2>,
    flow3: Flow<T3>,
    flow4: Flow<T4>,
    flow5: Flow<T5>,
    flow6: Flow<T6>,
    flow7: Flow<T7>,
    flow8: Flow<T8>,
    flow9: Flow<T9>,
    flow10: Flow<T10>,
): Flow<Tuple10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>> =
    combine(flow1, flow2, flow3, flow4, flow5, flow6, flow7, flow8, flow9, flow10) {
        Tuple10(
            it[0] as T1,
            it[1] as T2,
            it[2] as T3,
            it[3] as T4,
            it[4] as T5,
            it[5] as T6,
            it[6] as T7,
            it[7] as T8,
            it[8] as T9,
            it[9] as T10,
        )
    }

fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> combines(
    flow1: Flow<T1>,
    flow2: Flow<T2>,
    flow3: Flow<T3>,
    flow4: Flow<T4>,
    flow5: Flow<T5>,
    flow6: Flow<T6>,
    flow7: Flow<T7>,
    flow8: Flow<T8>,
    flow9: Flow<T9>,
    flow10: Flow<T10>,
    flow11: Flow<T11>,
): Flow<Tuple11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>> =
    combine(flow1, flow2, flow3, flow4, flow5, flow6, flow7, flow8, flow9, flow10, flow11) {
        Tuple11(
            it[0] as T1,
            it[1] as T2,
            it[2] as T3,
            it[3] as T4,
            it[4] as T5,
            it[5] as T6,
            it[6] as T7,
            it[7] as T8,
            it[8] as T9,
            it[9] as T10,
            it[10] as T11,
        )
    }

fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> combines(
    flow1: Flow<T1>,
    flow2: Flow<T2>,
    flow3: Flow<T3>,
    flow4: Flow<T4>,
    flow5: Flow<T5>,
    flow6: Flow<T6>,
    flow7: Flow<T7>,
    flow8: Flow<T8>,
    flow9: Flow<T9>,
    flow10: Flow<T10>,
    flow11: Flow<T11>,
    flow12: Flow<T12>,
): Flow<Tuple12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>> =
    combine(flow1, flow2, flow3, flow4, flow5, flow6, flow7, flow8, flow9, flow10, flow11, flow12) {
        Tuple12(
            it[0] as T1,
            it[1] as T2,
            it[2] as T3,
            it[3] as T4,
            it[4] as T5,
            it[5] as T6,
            it[6] as T7,
            it[7] as T8,
            it[8] as T9,
            it[9] as T10,
            it[10] as T11,
            it[11] as T12,
        )
    }

fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> combines(
    flow1: Flow<T1>,
    flow2: Flow<T2>,
    flow3: Flow<T3>,
    flow4: Flow<T4>,
    flow5: Flow<T5>,
    flow6: Flow<T6>,
    flow7: Flow<T7>,
    flow8: Flow<T8>,
    flow9: Flow<T9>,
    flow10: Flow<T10>,
    flow11: Flow<T11>,
    flow12: Flow<T12>,
    flow13: Flow<T13>,
): Flow<Tuple13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13>> =
    combine(flow1, flow2, flow3, flow4, flow5, flow6, flow7, flow8, flow9, flow10, flow11, flow12, flow13) {
        Tuple13(
            it[0] as T1,
            it[1] as T2,
            it[2] as T3,
            it[3] as T4,
            it[4] as T5,
            it[5] as T6,
            it[6] as T7,
            it[7] as T8,
            it[8] as T9,
            it[9] as T10,
            it[10] as T11,
            it[11] as T12,
            it[12] as T13,
        )
    }

fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> combines(
    flow1: Flow<T1>,
    flow2: Flow<T2>,
    flow3: Flow<T3>,
    flow4: Flow<T4>,
    flow5: Flow<T5>,
    flow6: Flow<T6>,
    flow7: Flow<T7>,
    flow8: Flow<T8>,
    flow9: Flow<T9>,
    flow10: Flow<T10>,
    flow11: Flow<T11>,
    flow12: Flow<T12>,
    flow13: Flow<T13>,
    flow14: Flow<T14>,
): Flow<Tuple14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14>> =
    combine(flow1, flow2, flow3, flow4, flow5, flow6, flow7, flow8, flow9, flow10, flow11, flow12, flow13, flow14) {
        Tuple14(
            it[0] as T1,
            it[1] as T2,
            it[2] as T3,
            it[3] as T4,
            it[4] as T5,
            it[5] as T6,
            it[6] as T7,
            it[7] as T8,
            it[8] as T9,
            it[9] as T10,
            it[10] as T11,
            it[11] as T12,
            it[12] as T13,
            it[13] as T14,
        )
    }

fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> combines(
    flow1: Flow<T1>,
    flow2: Flow<T2>,
    flow3: Flow<T3>,
    flow4: Flow<T4>,
    flow5: Flow<T5>,
    flow6: Flow<T6>,
    flow7: Flow<T7>,
    flow8: Flow<T8>,
    flow9: Flow<T9>,
    flow10: Flow<T10>,
    flow11: Flow<T11>,
    flow12: Flow<T12>,
    flow13: Flow<T13>,
    flow14: Flow<T14>,
    flow15: Flow<T15>,
): Flow<Tuple15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15>> =
    combine(flow1, flow2, flow3, flow4, flow5, flow6, flow7, flow8, flow9, flow10, flow11, flow12, flow13, flow14, flow15) {
        Tuple15(
            it[0] as T1,
            it[1] as T2,
            it[2] as T3,
            it[3] as T4,
            it[4] as T5,
            it[5] as T6,
            it[6] as T7,
            it[7] as T8,
            it[8] as T9,
            it[9] as T10,
            it[10] as T11,
            it[11] as T12,
            it[12] as T13,
            it[13] as T14,
            it[14] as T15,
        )
    }

fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> combines(
    flow1: Flow<T1>,
    flow2: Flow<T2>,
    flow3: Flow<T3>,
    flow4: Flow<T4>,
    flow5: Flow<T5>,
    flow6: Flow<T6>,
    flow7: Flow<T7>,
    flow8: Flow<T8>,
    flow9: Flow<T9>,
    flow10: Flow<T10>,
    flow11: Flow<T11>,
    flow12: Flow<T12>,
    flow13: Flow<T13>,
    flow14: Flow<T14>,
    flow15: Flow<T15>,
    flow16: Flow<T16>,
): Flow<Tuple16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16>> =
    combine(flow1, flow2, flow3, flow4, flow5, flow6, flow7, flow8, flow9, flow10, flow11, flow12, flow13, flow14, flow15, flow16) {
        Tuple16(
            it[0] as T1,
            it[1] as T2,
            it[2] as T3,
            it[3] as T4,
            it[4] as T5,
            it[5] as T6,
            it[6] as T7,
            it[7] as T8,
            it[8] as T9,
            it[9] as T10,
            it[10] as T11,
            it[11] as T12,
            it[12] as T13,
            it[13] as T14,
            it[14] as T15,
            it[15] as T16,
        )
    }

fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> combines(
    flow1: Flow<T1>,
    flow2: Flow<T2>,
    flow3: Flow<T3>,
    flow4: Flow<T4>,
    flow5: Flow<T5>,
    flow6: Flow<T6>,
    flow7: Flow<T7>,
    flow8: Flow<T8>,
    flow9: Flow<T9>,
    flow10: Flow<T10>,
    flow11: Flow<T11>,
    flow12: Flow<T12>,
    flow13: Flow<T13>,
    flow14: Flow<T14>,
    flow15: Flow<T15>,
    flow16: Flow<T16>,
    flow17: Flow<T17>,
): Flow<Tuple17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17>> =
    combine(flow1, flow2, flow3, flow4, flow5, flow6, flow7, flow8, flow9, flow10, flow11, flow12, flow13, flow14, flow15, flow16, flow17) {
        Tuple17(
            it[0] as T1,
            it[1] as T2,
            it[2] as T3,
            it[3] as T4,
            it[4] as T5,
            it[5] as T6,
            it[6] as T7,
            it[7] as T8,
            it[8] as T9,
            it[9] as T10,
            it[10] as T11,
            it[11] as T12,
            it[12] as T13,
            it[13] as T14,
            it[14] as T15,
            it[15] as T16,
            it[16] as T17,
        )
    }

fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> combines(
    flow1: Flow<T1>,
    flow2: Flow<T2>,
    flow3: Flow<T3>,
    flow4: Flow<T4>,
    flow5: Flow<T5>,
    flow6: Flow<T6>,
    flow7: Flow<T7>,
    flow8: Flow<T8>,
    flow9: Flow<T9>,
    flow10: Flow<T10>,
    flow11: Flow<T11>,
    flow12: Flow<T12>,
    flow13: Flow<T13>,
    flow14: Flow<T14>,
    flow15: Flow<T15>,
    flow16: Flow<T16>,
    flow17: Flow<T17>,
    flow18: Flow<T18>,
): Flow<Tuple18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18>> =
    combine(flow1, flow2, flow3, flow4, flow5, flow6, flow7, flow8, flow9, flow10, flow11, flow12, flow13, flow14, flow15, flow16, flow17, flow18) {
        Tuple18(
            it[0] as T1,
            it[1] as T2,
            it[2] as T3,
            it[3] as T4,
            it[4] as T5,
            it[5] as T6,
            it[6] as T7,
            it[7] as T8,
            it[8] as T9,
            it[9] as T10,
            it[10] as T11,
            it[11] as T12,
            it[12] as T13,
            it[13] as T14,
            it[14] as T15,
            it[15] as T16,
            it[16] as T17,
            it[17] as T18,
        )
    }

fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> combines(
    flow1: Flow<T1>,
    flow2: Flow<T2>,
    flow3: Flow<T3>,
    flow4: Flow<T4>,
    flow5: Flow<T5>,
    flow6: Flow<T6>,
    flow7: Flow<T7>,
    flow8: Flow<T8>,
    flow9: Flow<T9>,
    flow10: Flow<T10>,
    flow11: Flow<T11>,
    flow12: Flow<T12>,
    flow13: Flow<T13>,
    flow14: Flow<T14>,
    flow15: Flow<T15>,
    flow16: Flow<T16>,
    flow17: Flow<T17>,
    flow18: Flow<T18>,
    flow19: Flow<T19>,
): Flow<Tuple19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19>> =
    combine(flow1, flow2, flow3, flow4, flow5, flow6, flow7, flow8, flow9, flow10, flow11, flow12, flow13, flow14, flow15, flow16, flow17, flow18, flow19) {
        Tuple19(
            it[0] as T1,
            it[1] as T2,
            it[2] as T3,
            it[3] as T4,
            it[4] as T5,
            it[5] as T6,
            it[6] as T7,
            it[7] as T8,
            it[8] as T9,
            it[9] as T10,
            it[10] as T11,
            it[11] as T12,
            it[12] as T13,
            it[13] as T14,
            it[14] as T15,
            it[15] as T16,
            it[16] as T17,
            it[17] as T18,
            it[18] as T19,
        )
    }

fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> combines(
    flow1: Flow<T1>,
    flow2: Flow<T2>,
    flow3: Flow<T3>,
    flow4: Flow<T4>,
    flow5: Flow<T5>,
    flow6: Flow<T6>,
    flow7: Flow<T7>,
    flow8: Flow<T8>,
    flow9: Flow<T9>,
    flow10: Flow<T10>,
    flow11: Flow<T11>,
    flow12: Flow<T12>,
    flow13: Flow<T13>,
    flow14: Flow<T14>,
    flow15: Flow<T15>,
    flow16: Flow<T16>,
    flow17: Flow<T17>,
    flow18: Flow<T18>,
    flow19: Flow<T19>,
    flow20: Flow<T20>,
): Flow<Tuple20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20>> =
    combine(flow1, flow2, flow3, flow4, flow5, flow6, flow7, flow8, flow9, flow10, flow11, flow12, flow13, flow14, flow15, flow16, flow17, flow18, flow19, flow20) {
        Tuple20(
            it[0] as T1,
            it[1] as T2,
            it[2] as T3,
            it[3] as T4,
            it[4] as T5,
            it[5] as T6,
            it[6] as T7,
            it[7] as T8,
            it[8] as T9,
            it[9] as T10,
            it[10] as T11,
            it[11] as T12,
            it[12] as T13,
            it[13] as T14,
            it[14] as T15,
            it[15] as T16,
            it[16] as T17,
            it[17] as T18,
            it[18] as T19,
            it[19] as T20,
        )
    }

