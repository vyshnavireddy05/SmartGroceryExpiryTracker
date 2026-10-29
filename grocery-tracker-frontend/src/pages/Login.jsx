import { useState } from "react";
import { useNavigate } from "react-router-dom";
import AuthForm from "../components/AuthForm";
import { notifyAuthenticationChanged } from "../utils/auth";

export default function Login() {
const navigate = useNavigate();

const [formData, setFormData] = useState({
    email: "",
    password: "",
});

const [loading, setLoading] = useState(false);
const [error, setError] = useState("");

function handleChange(e) {
    const { name, value } = e.target;

    setFormData((previous) => ({
        ...previous,
        [name]: value,
    }));
}

async function handleLogin(e) {
    e.preventDefault();
    if (loading) return;
    setError("");
    setLoading(true);

    try {
        const response = await fetch("/api/auth/login", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify(formData),
        });

        const data = await response.json().catch(() => ({}));

        if (!response.ok) {
            throw new Error(
                data.message || "Invalid email or password."
            );
        }

        if (!data.token) {
            throw new Error(
                "Login succeeded, but the server did not return a token."
            );
        }

        localStorage.setItem("token", data.token);

        localStorage.setItem(
            "user",
            JSON.stringify({
                id: data.id,
                firstName: data.firstName,
                lastName: data.lastName,
                email: data.email,
            })
        );
        notifyAuthenticationChanged();

        navigate("/dashboard", { replace: true });
    } catch (err) {
        setError(
            err.message || "Unable to log in. Please try again."
        );
    } finally {
        setLoading(false);
    }
}

return (
    <AuthForm
        title="Login"
        fields={[
            {
                name: "email",
                placeholder: "Enter Email ID",
                type: "email",
                value: formData.email,
                onChange: handleChange,
                autoComplete: "email",
            },
            {
                name: "password",
                placeholder: "Enter Password",
                type: "password",
                value: formData.password,
                onChange: handleChange,
                autoComplete: "current-password",
            },
        ]}
        buttonText="Login"
        onSubmit={handleLogin}
        loading={loading}
        error={error}
        altText="Forgot Password?"
        altLink="/forgot-password"
        altLinkText="Reset Password"
        footerText="Don't have an account?"
        footerLink="/Signup"
        footerLinkText="Sign up"
    />
);
}