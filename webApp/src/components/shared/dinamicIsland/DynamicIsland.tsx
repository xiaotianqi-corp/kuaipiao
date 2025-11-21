'use client'

import * as Dialog from '@radix-ui/react-dialog'
import { motion } from 'framer-motion'
import { useEffect, useState, type ReactNode } from 'react'
import { Button } from '@/components/ui/button'

interface Action {
    label: string
    icon?: ReactNode
    variant?: 'default' | 'primary' | 'danger'
    onClick: () => void
    shouldClose?: boolean
}

export interface DynamicIslandDialogProps {
    id?: string | number
    title: string | ReactNode
    description?: string | ReactNode
    icon?: ReactNode
    actions?: Action[]
    position?:
        | 'top-center'
        | 'bottom-center'
        | 'top-left'
        | 'top-right'
        | 'bottom-left'
        | 'bottom-right'
    open: boolean
    onOpenChange?: (open: boolean) => void
    showProgress?: boolean
    autoClose?: number
}

const positionStyles: Record<
    NonNullable<DynamicIslandDialogProps['position']>,
    string
> = {
    'top-center': 'top-6 left-1/2 -translate-x-1/2',
    'bottom-center': 'bottom-6 left-1/2 -translate-x-1/2',
    'top-left': 'top-6 left-6',
    'top-right': 'top-6 right-6',
    'bottom-left': 'bottom-6 left-6',
    'bottom-right': 'bottom-6 right-6',
}

export const DynamicIsland = ({
                                  title,
                                  description,
                                  icon,
                                  actions = [],
                                  position = 'top-center',
                                  open,
                                  onOpenChange,
                                  showProgress = false,
                                  autoClose,
                              }: DynamicIslandDialogProps) => {
    const [expanded, setExpanded] = useState(false)

    useEffect(() => {
        if (!open || !autoClose) return

        const timer = setTimeout(() => {
            onOpenChange?.(false)
        }, autoClose)

        return () => clearTimeout(timer)
    }, [open, autoClose, onOpenChange])

    /** Pequeña animación tras abrir */
    useEffect(() => {
        if (open) {
            const timer = setTimeout(() => setExpanded(true), 150)
            return () => clearTimeout(timer)
        }
        setExpanded(false)
    }, [open])

    const handleActionClick = (action: Action) => {
        action.onClick()
        if (action.shouldClose) {
            onOpenChange?.(false)
        }
    }

    return (
        <Dialog.Root open={open} onOpenChange={onOpenChange}>
            <Dialog.Portal>
                <motion.div
                    initial={{ scale: 0.7, opacity: 0 }}
                    animate={{ scale: expanded ? 1 : 0.7, opacity: open ? 1 : 0 }}
                    exit={{ scale: 0.9, opacity: 0 }}
                    transition={{ type: 'spring', stiffness: 260, damping: 25 }}
                    style={{ transformOrigin: 'center' }}
                    className={`fixed z-50 flex flex-row items-center gap-x-4 w-fit max-w-[95vw] overflow-hidden
            bg-black px-4 py-3 text-white shadow-xl backdrop-blur-sm rounded-full
            ${positionStyles[position]} pointer-events-auto`}
                >
                    <div className="flex items-center gap-2 min-w-0">
                        <div className="bg-white/10 rounded-xl w-8 h-8 flex items-center justify-center shrink-0">
                            {icon}
                        </div>
                        <div className="flex flex-col min-w-0">
                            <Dialog.Title className="font-semibold text-sm truncate">
                                {title}
                            </Dialog.Title>
                            {description && (
                                <Dialog.Description className="text-xs text-white/70 truncate">
                                    {description}
                                </Dialog.Description>
                            )}
                        </div>
                    </div>

                    {actions.length > 0 && expanded && (
                        <div className="flex flex-nowrap items-center gap-2">
                            {actions.map((action, idx) => (
                                <Button
                                    key={idx}
                                    onClick={(e) => {
                                        e.stopPropagation()
                                        handleActionClick(action)
                                    }}
                                    className={`flex items-center gap-2 px-4 py-1 rounded-full text-white transition
                    ${
                                        action.variant === 'primary'
                                            ? 'bg-fuchsia-600 hover:bg-fuchsia-700'
                                            : action.variant === 'danger'
                                                ? 'bg-red-600 hover:bg-red-700'
                                                : 'bg-white/10 hover:bg-white/20'
                                    }`}
                                >
                                    {action.icon}
                                    {action.label}
                                </Button>
                            ))}
                        </div>
                    )}

                    {showProgress && (
                        <motion.div
                            initial={{ width: '0%' }}
                            animate={{ width: '100%' }}
                            transition={{ duration: 10, ease: 'linear' }}
                            className="absolute bottom-0 left-0 h-1 bg-white/20"
                        />
                    )}
                </motion.div>
            </Dialog.Portal>
        </Dialog.Root>
    )
}
