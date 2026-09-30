# Opponify Identity & Authentication

Phase 10C provides the Android authentication boundary. Firebase Authentication is the identity provider; the Opponify User and Player Profile remain separate backend domain resources.

The module supports email/password account creation and sign-in, password reset, Firebase ID-token retrieval for backend authorization, and phone-number verification/linking for an already authenticated account.

`google-services.json` and production Firebase configuration are intentionally externalized and are not committed to the repository.
