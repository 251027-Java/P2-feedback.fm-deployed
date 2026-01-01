/* 
This file is part of the process of allowing for the creation of a frontend
image with environment variable configuration. The idea is that the frontend
image should be able to be configured without having to rebuild the image.

See the original issue for more information:
https://github.com/251027-Java/P2-feedback.fm-deployed/issues/70

The purpose of this variable is be a single source to obtain environment passed
through vite. `import.meta.env` should NOT be accessed by other frontend asset
files.

When running `npm run build`, this file will not be merged with other code.
The configuration for that logic can be found in `vite.config.ts`.
*/
export const environment = { ...import.meta.env };
