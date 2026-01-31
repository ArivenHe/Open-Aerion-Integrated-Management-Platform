# Openfsd Euroscope Patch
## How to use?
replace the value of ``TARGET_JWT_URL`` in ``config.h`` and then compile.

## Notes
Due to implementation limitations in the current version, the URL must be shorter than the original VATSIM authentication API endpoint length (https://auth.vatsim.net/api/fsd-jwt). A new version that lifts this restriction is currently in development.

You are advised to ensure that any generated URL remains within this length constraint until the updated version is released.