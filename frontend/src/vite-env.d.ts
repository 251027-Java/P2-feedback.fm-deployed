/// <reference types="vite/client" />

declare module '*.css' {
  const content: Record<string, string>;
  export default content;
}

// https://vite.dev/guide/env-and-mode#intellisense-for-typescript
// biome-ignore lint/correctness/noUnusedVariables: this is for import.meta
interface ImportMetaEnv {
  readonly VITE_API_URL: string;
}
