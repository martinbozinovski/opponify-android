package com.opponify.common.architecture

sealed interface AppError {
    data object NetworkUnavailable : AppError
    data object Unauthorized : AppError
    data object Forbidden : AppError
    data object NotFound : AppError
    data object Conflict : AppError
    data object Validation : AppError
    data object RateLimited : AppError
    data object Unknown : AppError
}
