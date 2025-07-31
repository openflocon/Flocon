package io.github.openflocon.flocondesktop.common

sealed class Either<out F, out S> {
    inline fun <X> fold(
        doOnFailure: (F) -> X,
        doOnSuccess: (S) -> X,
    ): X = when (this) {
        is Success -> doOnSuccess(value)
        is Failure -> doOnFailure(value)
    }

    inline fun <X> map(
        mapError: (F) -> X,
        mapSuccess: (S) -> X,
    ): X = when (this) {
        is Success -> mapSuccess(value)
        is Failure -> mapError(value)
    }

    inline fun <X> mapSuccess(f: (S) -> X): Either<F, X> = when (this) {
        is Success -> Success(f(value))
        is Failure -> Failure(value)
    }

    inline fun <X> mapFailure(f: (F) -> X): Either<X, S> = when (this) {
        is Success -> Success(value)
        is Failure -> Failure(f(value))
    }

    inline fun alsoSuccess(block: (S) -> Unit): Either<F, S> {
        when (this) {
            is Success -> block(this.value)
            is Failure -> Unit
        }
        return this
    }

    inline fun alsoFailure(block: (F) -> Unit): Either<F, S> {
        when (this) {
            is Failure -> block(this.value)
            is Success -> Unit
        }
        return this
    }

    companion object {
        inline fun <T> catch(body: () -> T): Either<Throwable, T> = try {
            Success(body())
        } catch (t: Throwable) {
            Failure(t)
        }
    }
}

data class Failure<out T>(
    val value: T,
) : Either<T, Nothing>()

data class Success<out T>(
    val value: T,
) : Either<Nothing, T>()

fun <T> T.failure() = Failure(this)

fun <T> T.success() = Success(this)

fun <T> Either<T, T>.merge(): T = when (this) {
    is Success -> value
    is Failure -> value
}

fun <F, S> Either<F, S>.getOrElse(default: S): S = when (this) {
    is Success -> value
    is Failure -> default
}

inline fun <F, S> Either<F, S>.getOrRun(doOnFailure: (F) -> S): S = this.fold(
    doOnFailure = doOnFailure,
    doOnSuccess = { it },
)

fun <F, S> Either<F, S>.getOrNull(): S? = when (this) {
    is Success -> value
    is Failure -> null
}

inline fun <ERROR, SUCCESS, NEW_SUCCESS> Either<ERROR, SUCCESS>.then(
    block: (SUCCESS) -> Either<ERROR, NEW_SUCCESS>,
): Either<ERROR, NEW_SUCCESS> = when (this) {
    is Failure -> this
    is Success -> block(value)
}

inline fun <ERROR, SUCCESS, NEW_ERROR> Either<ERROR, SUCCESS>.ifErrorThen(
    block: (ERROR) -> Either<NEW_ERROR, SUCCESS>,
): Either<NEW_ERROR, SUCCESS> = when (this) {
    is Failure -> block(value)
    is Success -> this
}
