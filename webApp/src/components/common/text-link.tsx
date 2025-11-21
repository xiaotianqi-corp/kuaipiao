import { cn } from '@/lib/utils';
import { Link } from 'react-router-dom';
import { ComponentProps } from 'react';

type LinkProps = ComponentProps<typeof Link> & {
    underlineOnHover?: boolean;
    as?: 'a' | 'button';
};

export default function TextLink({
                                     className = '',
                                     children,
                                     underlineOnHover = false,
                                     as = 'a',
                                     ...props
                                 }: LinkProps) {
    const Comp: any = as === 'button' ? 'button' : Link;

    return (
        <Comp
            className={cn(
                'relative inline-block text-foreground decoration-neutral-300 underline-offset-4 transition-colors duration-300 ease-out dark:decoration-neutral-500',
                underlineOnHover
                    ? 'no-underline after:absolute after:bottom-0 after:left-0 after:h-[1px] after:w-0 after:bg-current after:transition-all after:duration-300 hover:after:w-full'
                    : 'no-underline after:absolute after:bottom-0 after:left-0 after:h-[1px] after:w-0 after:bg-current after:transition-all after:duration-300 hover:after:w-full',
                className
            )}
            {...props}
        >
            {children}
        </Comp>
    );
}

