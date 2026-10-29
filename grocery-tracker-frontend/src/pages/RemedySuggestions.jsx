
import { useEffect, useState } from "react";
import styles from "../styling/RemedySuggestions.module.css";


const FOOD_IMAGES = [
    "https://images.unsplash.com/photo-1547592180-85f173990554?auto=format&fit=crop&w=1000&q=85",
    "https://images.unsplash.com/photo-1540189549336-e6e99c3679fe?auto=format&fit=crop&w=1000&q=85",
    "https://images.unsplash.com/photo-1565299624946-b28f40a0ae38?auto=format&fit=crop&w=1000&q=85",
    "https://images.unsplash.com/photo-1563379926898-05f4575a45d8?auto=format&fit=crop&w=1000&q=85",
    "https://images.unsplash.com/photo-1551183053-bf91a1d81141?auto=format&fit=crop&w=1000&q=85",
];


const getRecipeImage = (recipe, index) => {
    const name = recipe?.name?.toLowerCase() || "";

    if (name.includes("salad") || name.includes("vegetable")) {
        return FOOD_IMAGES[1];
    }

    if (name.includes("toast") || name.includes("sandwich")) {
        return FOOD_IMAGES[2];
    }

    if (name.includes("pasta") || name.includes("noodle")) {
        return FOOD_IMAGES[3];
    }

    if (name.includes("rice") || name.includes("bowl")) {
        return FOOD_IMAGES[4];
    }

    return FOOD_IMAGES[index % FOOD_IMAGES.length];
};

const RemedySuggestions = () => {
    const [recipes, setRecipes] = useState({
        canMakeNow: [],
        needsAdditionalIngredients: [],
    });

    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    useEffect(() => {
        fetchRecipes();
    }, []);


const fetchRecipes = async () => {
    try {
        setLoading(true);
        setError("");

        const token = localStorage.getItem("token");

        if (!token) {
            throw new Error("Please log in to view your recipe suggestions.");
        }

        const response = await fetch("/api/recipe/recommendations", {
            method: "GET",
            headers: {
                Authorization: `Bearer ${token}`,
                "Content-Type": "application/json",
            },
        });

        if (response.status === 401 || response.status === 403) {
            throw new Error("Your session has expired. Please log in again.");
        }

        if (!response.ok) {
            throw new Error("Failed to fetch recipe recommendations.");
        }

        const data = await response.json();

        console.log("Recipe recommendations:", data);

        setRecipes({
            canMakeNow: Array.isArray(data.canMakeNow)
                ? data.canMakeNow
                : [],

            needsAdditionalIngredients: Array.isArray(
                data.needsAdditionalIngredients
            )
                ? data.needsAdditionalIngredients
                : [],
        });
    } catch (error) {
        console.error("Recipe API error:", error);

        setError(
            error.message ||
            "Unable to load recipe suggestions. Please try again."
        );
    } finally {
        setLoading(false);
    }
};


    /*
     * Combine both categories into ONE list.
     *
     * Available recipes come first.
     * Recipes requiring additional ingredients come after them.
     */
    const allRecipes = [
        ...recipes.canMakeNow.map((recipe) => ({
            ...recipe,
            available: true,
        })),

        ...recipes.needsAdditionalIngredients.map((recipe) => ({
            ...recipe,
            available: false,
        })),
    ];

    // =========================================================
    // LOADING
    // =========================================================

    if (loading) {
        return (
            <div className={styles.page}>
                <div className={styles.pageHeader}>
                    <div>
                        <span className={styles.eyebrow}>
                            SMART MEAL PLANNER
                        </span>

                        <h1>Recipe Suggestions</h1>

                        <p>
                            Turn the groceries in your inventory
                            into simple meals before they expire.
                        </p>
                    </div>
                </div>

                <div className={styles.loadingContainer}>
                    <div className={styles.spinner}></div>

                    <h3>Finding recipes for you</h3>

                    <p>
                        Checking your available groceries...
                    </p>
                </div>
            </div>
        );
    }

    // =========================================================
    // ERROR
    // =========================================================

    if (error) {
        return (
            <div className={styles.page}>
                <div className={styles.pageHeader}>
                    <div>
                        <span className={styles.eyebrow}>
                            SMART MEAL PLANNER
                        </span>

                        <h1>Recipe Suggestions</h1>

                        <p>
                            Personalized meal ideas from your
                            grocery inventory.
                        </p>
                    </div>
                </div>

                <div className={styles.errorContainer}>
                    <div className={styles.errorIcon}>
                        !
                    </div>

                    <h2>Couldn't load your recipes</h2>

                    <p>{error}</p>

                    <button
                        className={styles.retryButton}
                        onClick={fetchRecipes}
                    >
                        Try Again
                    </button>
                </div>
            </div>
        );
    }

    // =========================================================
    // EMPTY STATE
    // =========================================================

    if (allRecipes.length === 0) {
        return (
            <div className={styles.page}>
                <div className={styles.pageHeader}>
                    <div>
                        <span className={styles.eyebrow}>
                            SMART MEAL PLANNER
                        </span>

                        <h1>Recipe Suggestions</h1>

                        <p>
                            Personalized meal ideas from your
                            grocery inventory.
                        </p>
                    </div>

                    <button
                        className={styles.refreshButton}
                        onClick={fetchRecipes}
                    >
                        ↻ Refresh
                    </button>
                </div>

                <div className={styles.emptyContainer}>
                    <div className={styles.emptyIllustration}>
                        🍳
                    </div>

                    <h2>No recipe ideas yet</h2>

                    <p>
                        Add fresh or expiring groceries to your
                        inventory and we'll find useful recipes
                        you can prepare.
                    </p>

                    <span>
                        Expired items are automatically excluded.
                    </span>
                </div>
            </div>
        );
    }

    // =========================================================
    // MAIN PAGE
    // =========================================================

    return (
        <div className={styles.page}>

            {/* =================================================
                PAGE HEADER
            ================================================= */}

            <div className={styles.pageHeader}>
                <div>
                    <span className={styles.eyebrow}>
                        SMART MEAL PLANNER
                    </span>

                    <h1>Recipe Suggestions</h1>

                    <p>
                        Make something delicious with what you
                        already have.
                    </p>
                </div>

                <button
                    className={styles.refreshButton}
                    onClick={fetchRecipes}
                >
                    <span>↻</span>
                    Refresh
                </button>
            </div>

            {/* =================================================
                RECIPE SECTION
            ================================================= */}

            <section className={styles.recipeSection}>
                <div className={styles.sectionHeading}>
                    <div>
                        <h2>What can you make?</h2>

                        <p>
                            Recipes are based on your current
                            inventory, with expiring items given
                            priority.
                        </p>
                    </div>

                    <div className={styles.recipeCount}>
                        {allRecipes.length}

                        <span>
                            {allRecipes.length === 1
                                ? " recipe"
                                : " recipes"}
                        </span>
                    </div>
                </div>

                {/* =================================================
                    UNIFIED RECIPE GRID
                ================================================= */}

                <div className={styles.recipeGrid}>
                    {allRecipes.map((recipe, index) => (
                        <RecipeCard
                            key={`${recipe.name}-${index}`}
                            recipe={recipe}
                            index={index}
                        />
                    ))}
                </div>
            </section>
        </div>
    );
};

// =============================================================
// RECIPE CARD
// =============================================================

const RecipeCard = ({ recipe, index }) => {
    const image = getRecipeImage(recipe, index);

    return (
        <article className={styles.recipeCard}>

            {/* =================================================
                IMAGE
            ================================================= */}

            <div className={styles.imageContainer}>
                <img
                    src={image}
                    alt={recipe.name}
                    className={styles.recipeImage}
                    loading="lazy"
                    onError={(event) => {
                        event.currentTarget.src =
                            FOOD_IMAGES[
                                index % FOOD_IMAGES.length
                            ];
                    }}
                />

                {/* Dark image overlay */}
                <div className={styles.imageOverlay}></div>

                {/* Availability badge ON IMAGE */}
                <div
                    className={
                        recipe.available
                            ? styles.availableTag
                            : styles.additionalTag
                    }
                >
                    <span className={styles.tagDot}></span>

                    {recipe.available
                        ? "Available"
                        : "More ingredients needed"}
                </div>

                {/* Recipe category */}
                <div className={styles.recipeImageLabel}>
                    {recipe.available
                        ? "READY TO MAKE"
                        : "WORTH TRYING"}
                </div>
            </div>

            {/* =================================================
                CARD CONTENT
            ================================================= */}

            <div className={styles.recipeContent}>

                {/* Title */}
                <div className={styles.recipeTitleArea}>
                    <h3>{recipe.name}</h3>
                </div>

                {/* =================================================
                    INGREDIENTS
                ================================================= */}

                {recipe.ingredients?.length > 0 && (
                    <div className={styles.contentSection}>
                        <h4>Ingredients</h4>

                        <div className={styles.ingredientList}>
                            {recipe.ingredients.map(
                                (ingredient, ingredientIndex) => (
                                    <div
                                        className={
                                            styles.ingredientRow
                                        }
                                        key={ingredientIndex}
                                    >
                                        <span>
                                            {ingredient.itemName}
                                        </span>

                                        <strong>
                                            {ingredient.quantity}{" "}
                                            {ingredient.unit}
                                        </strong>
                                    </div>
                                )
                            )}
                        </div>
                    </div>
                )}

                {/* =================================================
                    MISSING INGREDIENTS
                ================================================= */}

                {!recipe.available &&
                    recipe.missingIngredients?.length > 0 && (
                        <div className={styles.missingBox}>
                            <div
                                className={
                                    styles.missingHeader
                                }
                            >
                                <span
                                    className={
                                        styles.missingIcon
                                    }
                                >
                                    +
                                </span>

                                <div>
                                    <h4>You need</h4>

                                    <p>
                                        A few more ingredients
                                    </p>
                                </div>
                            </div>

                            <div className={styles.missingList}>
                                {recipe.missingIngredients.map(
                                    (
                                        ingredient,
                                        ingredientIndex
                                    ) => (
                                        <div
                                            className={
                                                styles.missingRow
                                            }
                                            key={ingredientIndex}
                                        >
                                            <span>
                                                {
                                                    ingredient.itemName
                                                }
                                            </span>

                                            <strong>
                                                {
                                                    ingredient.quantity
                                                }{" "}
                                                {
                                                    ingredient.unit
                                                }
                                            </strong>
                                        </div>
                                    )
                                )}
                            </div>
                        </div>
                    )}

                {/* =================================================
                    PREPARATION
                ================================================= */}

                {recipe.preparationSteps?.length > 0 && (
                    <div className={styles.contentSection}>
                        <h4>How to prepare</h4>

                        <ol className={styles.stepsList}>
                            {recipe.preparationSteps.map(
                                (step, stepIndex) => (
                                    <li key={stepIndex}>
                                        <span
                                            className={
                                                styles.stepNumber
                                            }
                                        >
                                            {stepIndex + 1}
                                        </span>

                                        <span>{step}</span>
                                    </li>
                                )
                            )}
                        </ol>
                    </div>
                )}

                {/* =================================================
                    SAFETY
                ================================================= */}

                {recipe.safetyMeasures?.length > 0 && (
                    <div className={styles.safetyBox}>
                        <div className={styles.safetyTitle}>
                            🛡️ Food safety
                        </div>

                        <ul>
                            {recipe.safetyMeasures.map(
                                (
                                    measure,
                                    safetyIndex
                                ) => (
                                    <li key={safetyIndex}>
                                        {measure}
                                    </li>
                                )
                            )}
                        </ul>
                    </div>
                )}
            </div>
        </article>
    );
};

export default RemedySuggestions;