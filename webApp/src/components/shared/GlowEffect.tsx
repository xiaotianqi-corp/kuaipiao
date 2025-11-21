const GlowEffect = () => {
    return (
        <picture
            data-sentry-element="picture"
            data-sentry-source-file="page.tsx"
            className="glow-effect"
        >
            <source
                srcSet="/glow.webp"
                type="image/webp"
            />
            <img
                alt=""
                data-sentry-element="Image"
                data-sentry-source-file="page.tsx"
                width={1800}
                height={1800}
                decoding="async"
                data-nimg="1"
                className="pointer-events-none absolute left-1/2 top-1/2 z-10 h-full max-w-none -translate-x-1/2 -translate-y-1/2 select-none mix-blend-overlay dark:hidden"
                style={{ color: 'transparent' }}
                src="/glow.webp"
                loading="lazy"
            />
        </picture>
    );
};

export default GlowEffect;