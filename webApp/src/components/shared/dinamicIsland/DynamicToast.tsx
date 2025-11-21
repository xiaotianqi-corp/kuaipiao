import { useCallback, useMemo, useState, useEffect } from 'react'
import type { ReactNode } from 'react'
import { DynamicIsland } from './DynamicIsland'

interface Action {
    label: string
    icon?: ReactNode
    variant?: 'default' | 'primary' | 'danger'
    onClick: () => void
    shouldClose?: boolean
}

interface DialogData {
    title: string | ReactNode
    description?: string | ReactNode
    icon?: ReactNode
    actions?: Action[]
    position?: 'top-center' | 'bottom-center' | 'top-left' | 'top-right' | 'bottom-left' | 'bottom-right'
    showProgress?: boolean
    autoClose?: number;
    onClose?: () => void;
}

export const useDynamicDialog = () => {
    const [isOpen, setIsOpen] = useState(false)
    const [dialogProps, setDialogProps] = useState<DialogData | null>(null)

    const showDialog = useCallback((props: DialogData) => {
        setDialogProps(props)
        setIsOpen(true)
    }, [])

    const closeDialog = useCallback(() => {
        setIsOpen(false)
    }, [])

    const DialogComponent = useMemo(() => {
        return () =>
            dialogProps ? (
                <DynamicIsland
                    {...dialogProps}
                    open={isOpen}
                    onOpenChange={setIsOpen}
                />
            ) : null
    }, [dialogProps, isOpen])

    useEffect(() => {
        if (!dialogProps?.autoClose || !isOpen) return

        const timer = setTimeout(() => {
            closeDialog()
            dialogProps.onClose?.()
        }, dialogProps.autoClose)

        return () => clearTimeout(timer)
    }, [dialogProps, isOpen, closeDialog])

    return {
        showDialog,
        closeDialog,
        DialogComponent,
    }
}
