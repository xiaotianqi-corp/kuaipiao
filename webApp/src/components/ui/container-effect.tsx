import * as React from "react";
import { cn } from "@/lib/utils"; // tu helper para combinar clases

interface ContainerEffectProps {
    children: React.ReactNode;
    className?: string;
}

export const ContainerEffect: React.FC<ContainerEffectProps> = ({
                                                                            children,
                                                                            className,
                                                                        }) => {
    return <div className={cn("flex w-full flex-col space-y-2", className)}>{children}</div>;
};
