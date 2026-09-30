package com.opponify.database

/**
 * Describes whether a local value may be used for a particular operation.
 * The backend remains authoritative for consequential actions.
 */
enum class LocalDataPolicy {
    CACHEABLE_READ,
    DRAFT_ONLY,
    SERVER_AUTHORITATIVE,
}
