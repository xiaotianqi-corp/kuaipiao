import { useEffect } from 'react';

interface MetaTagsProps {
    title?: string;
    description?: string;
    keywords?: string;
    author?: string;
    viewport?: string;
    charset?: string;
    favicon?: string;
    preconnects?: string[];
    stylesheets?: string[];
    ogTitle?: string;
    ogDescription?: string;
    ogImage?: string;
    ogUrl?: string;
}

export default function MetaTags({
                                     title = 'App',
                                     description,
                                     keywords,
                                     author,
                                     viewport = 'width=device-width, initial-scale=1',
                                     charset = 'utf-8',
                                     favicon,
                                     preconnects = [],
                                     stylesheets = [],
                                     ogTitle,
                                     ogDescription,
                                     ogImage,
                                     ogUrl
                                 }: MetaTagsProps) {
    useEffect(() => {

        document.title = title;

        const updateMetaTag = (name: string, content: string, attribute: 'name' | 'property' = 'name') => {
            let meta = document.querySelector(`meta[${attribute}="${name}"]`) as HTMLMetaElement;
            if (!meta) {
                meta = document.createElement('meta');
                meta.setAttribute(attribute, name);
                document.head.appendChild(meta);
            }
            meta.content = content;
        };

        const createLinkElement = (rel: string, href: string, additionalAttributes?: Record<string, string>) => {

            const existingLink = document.querySelector(`link[rel="${rel}"][href="${href}"]`);
            if (existingLink) return existingLink;

            const link = document.createElement('link');
            link.rel = rel;
            link.href = href;

            if (additionalAttributes) {
                Object.entries(additionalAttributes).forEach(([key, value]) => {
                    link.setAttribute(key, value);
                });
            }

            document.head.appendChild(link);
            return link;
        };

        const createdElements: HTMLElement[] = [];

        let charsetMeta = document.querySelector('meta[charset]') as HTMLMetaElement;
        if (!charsetMeta) {
            charsetMeta = document.createElement('meta');
            charsetMeta.charset = charset;
            document.head.appendChild(charsetMeta);
            createdElements.push(charsetMeta);
        }

        updateMetaTag('viewport', viewport);

        if (description) updateMetaTag('description', description);
        if (keywords) updateMetaTag('keywords', keywords);
        if (author) updateMetaTag('author', author);

        if (ogTitle) updateMetaTag('og:title', ogTitle, 'property');
        if (ogDescription) updateMetaTag('og:description', ogDescription, 'property');
        if (ogImage) updateMetaTag('og:image', ogImage, 'property');
        if (ogUrl) updateMetaTag('og:url', ogUrl, 'property');

        if (favicon) {
            const faviconLink = createLinkElement('icon', favicon);
            createdElements.push(faviconLink as HTMLElement);
        }

        preconnects.forEach(url => {
            const preconnectLink = createLinkElement('preconnect', url);
            createdElements.push(preconnectLink as HTMLElement);
        });

        stylesheets.forEach(url => {
            const stylesheetLink = createLinkElement('stylesheet', url);
            createdElements.push(stylesheetLink as HTMLElement);
        });

        return () => {
            createdElements.forEach(element => {
                if (element.parentNode) {
                    element.parentNode.removeChild(element);
                }
            });
        };
    }, [title, description, keywords, author, viewport, charset, favicon, preconnects, stylesheets, ogTitle, ogDescription, ogImage, ogUrl]);

    return null;
}