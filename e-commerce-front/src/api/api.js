import axios from "axios";

const api = axios.create({
    baseURL: `${import.meta.env.VITE_BACK_END_URL}/api`,
    withCredentials: true,
});

// Dodaj interceptor, aby dodać token JWT do nagłówków wszystkich żądań
api.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem("auth") ? JSON.parse(localStorage.getItem("auth")).jwtToken : null;
        if (token) {
            config.headers["Authorization"] = `Bearer ${token}`;
        }
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);

export default api;