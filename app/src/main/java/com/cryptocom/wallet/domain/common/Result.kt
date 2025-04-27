package com.cryptocom.wallet.domain.common

/**
 * A generic class that holds a value with its loading status.
 * @param <T> The type of the data.
 */
sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val exception: Throwable) : Result<Nothing>()
    object Loading : Result<Nothing>() // Optional: Add a loading state if needed across layers
}

/**
 * Extension function to map Result<T> to Result<R>.
 */
inline fun <T, R> Result<T>.map(transform: (T) -> R): Result<R> {
    return when (this) {
        is Result.Success -> Result.Success(transform(data))
        is Result.Error -> Result.Error(exception)
        is Result.Loading -> Result.Loading
    }
}

/**
 * Extension function to execute an action if the Result is Success.
 */
inline fun <T> Result<T>.onSuccess(action: (T) -> Unit): Result<T> {
    if (this is Result.Success) {
        action(data)
    }
    return this
}

/**
 * Extension function to execute an action if the Result is Error.
 */
inline fun <T> Result<T>.onError(action: (Throwable) -> Unit): Result<T> {
    if (this is Result.Error) {
        action(exception)
    }
    return this
}

/**
 * Extension function to get the data if Success, or null otherwise.
 */
fun <T> Result<T>.getOrNull(): T? {
    return (this as? Result.Success)?.data
}

/**
 * Extension function to get the exception if Error, or null otherwise.
 */
fun <T> Result<T>.exceptionOrNull(): Throwable? {
    return (this as? Result.Error)?.exception
} 