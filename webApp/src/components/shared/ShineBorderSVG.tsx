"use client"

import { useEffect, useRef } from 'react';

interface ShineBorderSVGProps {
    /**
     * Color of the border, can be a single color or an array of colors
     * @default ["#A07CFE", "#FE8FB5", "#FFBE7B"]
     */
    shineColor?: string | string[];
    /**
     * Duration of the animation in seconds
     * @default 4
     */
    duration?: number;
    /**
     * SVG path data string
     */
    pathD: string;
    /**
     * Width of the stroke in pixels
     * @default 2
     */
    strokeWidth?: number;
}

export function ShineBorderSVG({
                                   shineColor = ["#A07CFE", "#FE8FB5", "#FFBE7B"],
                                   duration = 4,
                                   pathD,
                                   strokeWidth = 2
                               }: ShineBorderSVGProps) {
    const canvasRef = useRef<HTMLCanvasElement>(null);
    const animationRef = useRef<number | null>(null);

    useEffect(() => {
        const canvas = canvasRef.current;
        if (!canvas || !pathD) return;

        const ctx = canvas.getContext('2d');
        if (!ctx) return;

        const dpr = window.devicePixelRatio || 1;

        // Configurar canvas con DPR
        const rect = canvas.getBoundingClientRect();
        canvas.width = rect.width * dpr;
        canvas.height = rect.height * dpr;
        ctx.scale(dpr, dpr);

        // Crear el path desde el string SVG
        const path = new Path2D(pathD);

        const startTime = Date.now();

        const animate = () => {
            const elapsed = (Date.now() - startTime) / 1000;
            const progress = (elapsed % duration) / duration;

            ctx.clearRect(0, 0, rect.width, rect.height);

            // Crear gradiente animado
            const gradient = ctx.createLinearGradient(
                progress * rect.width * 2 - rect.width,
                0,
                progress * rect.width * 2,
                0
            );

            // Normalizar shineColor a array
            const colors = Array.isArray(shineColor) ? shineColor : [shineColor];

            // Agregar colores al gradiente
            gradient.addColorStop(0, 'transparent');
            colors.forEach((color, i) => {
                const stop = 0.3 + (i * 0.4 / colors.length);
                gradient.addColorStop(stop, color);
            });
            gradient.addColorStop(1, 'transparent');

            // Aplicar el gradiente al path
            ctx.strokeStyle = gradient;
            ctx.lineWidth = strokeWidth;
            ctx.stroke(path);

            animationRef.current = requestAnimationFrame(animate);
        };

        animate();

        return () => {
            if (animationRef.current !== null) {
                cancelAnimationFrame(animationRef.current);
            }
        };
    }, [shineColor, duration, pathD, strokeWidth]);

    return <canvas ref={canvasRef} className="absolute inset-0 h-full w-full" aria-hidden="true" />;
}