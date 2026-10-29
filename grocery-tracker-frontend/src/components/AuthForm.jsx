import { Link } from "react-router-dom";
import styles from "../styling/AuthForm.module.css";

export default function AuthForm({
title,
fields,
buttonText,
onSubmit,
loading = false,
error = "",
altText,
altLink,
altLinkText,
footerText,
footerLink,
footerLinkText,
}) {
return ( <div className={styles.authContainer}> <form className={styles.authForm} onSubmit={onSubmit}> <h2>{title}</h2>

```
            {fields.map((field) => (
                <input
                    key={field.name}
                    name={field.name}
                    type={field.type || "text"}
                    placeholder={field.placeholder}
                    value={field.value}
                    onChange={field.onChange}
                    autoComplete={field.autoComplete}
                    maxLength={field.maxLength}
                    required={field.required !== false}
                />
            ))}

            {altText && altLink && (
                <div className={styles.forgotPassword}>
                    <span>{altText} </span>
                    <Link to={altLink}>{altLinkText}</Link>
                </div>
            )}

            {error && (
                <p className={styles.errorMessage} role="alert">
                    {error}
                </p>
            )}

            <button
                type="submit"
                disabled={loading}
                className={styles.submitButton}
            >
                {loading ? "Please wait..." : buttonText}
            </button>

            {footerText && footerLink && (
                <p className={styles.footerText}>
                    {footerText}{" "}
                    <Link
                        to={footerLink}
                        className={styles.footerLink}
                    >
                        {footerLinkText}
                    </Link>
                </p>
            )}
        </form>
    </div>
);
}