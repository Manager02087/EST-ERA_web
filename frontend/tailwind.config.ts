import type { Config } from 'tailwindcss';

const config: Config = {
  content: ['./src/**/*.{js,ts,jsx,tsx,mdx}'],
  theme: {
    extend: {
      colors: {
        obsidian: '#0B0C10',
        surface: '#14161D',
        surfaceLight: '#1C1F29',
        gold: '#E5C158',
        goldDim: '#B99738',
      },
    },
  },
  plugins: [],
};
export default config;
