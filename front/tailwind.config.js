/** @type {import('tailwindcss').Config} */
export default {
    content: [
        "./index.html",
        "./src/**/*.{js,ts,jsx,tsx}"
    ],
    theme: {
        extend: {
            dropShadow: {
                'header': "20px 0 15px rgb(0, 0, 0)",
                'footer': "-20px 0 15px rgb(0, 0, 0)"
            }
        },
    },
    plugins: [],
}

