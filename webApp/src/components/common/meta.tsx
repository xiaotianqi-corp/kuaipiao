import React from 'react';

interface MetaProps {
    title?: string;
    description?: string;
}

export const Meta: React.FC<MetaProps> = ({
                                              title,
                                              description = 'Aplicación web moderna con React 19',
                                          }) => {
    return (
        <>
            {title && <title>{title}</title>}
            {description && <meta name="description" content={description} />}
        </>
    );
};
