import { useState } from "react";
import { useNavigate } from "react-router-dom";
import AuthForm from "../components/AuthForm";

export default function Singup() {
const navigate = useNavigate();
const [formData, setFormData] = useState({
    firstName: "",
    lastName: "",
    email: "",
    phoneNum: "",
    password: "",
    confirmPassword: "",
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

async function handleSignUp(e) {
    e.preventDefault();
    setError("");

    if (formData.password.length < 8) {
        setError("Password must contain at least 8 characters.");
        return;
    }

    if (formData.password.length > 72) {
        setError("Password cannot exceed 72 characters.");
        return;
    }

    if (formData.password !== formData.confirmPassword) {
        setError("Passwords do not match.");
        return;
    }

    setLoading(true);

    try {
        const { confirmPassword, ...registrationData } = formData;

        const response = await fetch("/api/auth/register", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify(registrationData),
        });

        const data = await response.json().catch(() => ({}));

        if (!response.ok) {
            throw new Error(
                data.message ||
                data.error ||
                "Unable to create your account."
            );
        }

        if (!data.token) {
            setError(
                "Your account was created. Please log in to continue."
            );
            navigate("/login", {
                replace: true,
                state: {
                    message: "Account created successfully. Please log in.",
                },
            });
            return;
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

        navigate("/dashboard", { replace: true });
    } catch (err) {
        setError(
            err.message || "Unable to sign up. Please try again."
        );
    } finally {
        setLoading(false);
    }
}

return (
    <AuthForm
        title="Create Account"
        fields={[
            {
                name: "firstName",
                placeholder: "First Name",
                type: "text",
                value: formData.firstName,
                onChange: handleChange,
                autoComplete: "given-name",
                maxLength: 50,
            },
            {
                name: "lastName",
                placeholder: "Last Name",
                type: "text",
                value: formData.lastName,
                onChange: handleChange,
                autoComplete: "family-name",
                maxLength: 50,
            },
            {
                name: "email",
                placeholder: "Email Address",
                type: "email",
                value: formData.email,
                onChange: handleChange,
                autoComplete: "email",
            },
            {
                name: "phoneNum",
                placeholder: "Phone Number",
                type: "tel",
                value: formData.phoneNum,
                onChange: handleChange,
                autoComplete: "tel",
            },
            {
                name: "password",
                placeholder: "Create Password",
                type: "password",
                value: formData.password,
                onChange: handleChange,
                autoComplete: "new-password",
                maxLength: 72,
            },
            {
                name: "confirmPassword",
                placeholder: "Confirm Password",
                type: "password",
                value: formData.confirmPassword,
                onChange: handleChange,
                autoComplete: "new-password",
                maxLength: 72,
            },
        ]}
        buttonText="Sign Up"
        onSubmit={handleSignUp}
        loading={loading}
        error={error}
        footerText="Already have an account?"
        footerLink="/login"
        footerLinkText="Login"
    />
);
}