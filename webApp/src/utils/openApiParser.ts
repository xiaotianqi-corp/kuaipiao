interface OpenAPISpec {
    openapi: string;
    info: {
        title: string;
        version: string;
        description: string;
    };
    servers: Array<{ url: string; description: string }>;
    paths: Record<string, any>;
    components: {
        schemas: Record<string, any>;
        securitySchemes: Record<string, any>;
    };
}

export async function fetchApiSpec(): Promise<OpenAPISpec> {
    const response = await fetch('http://localhost:8080/swagger/openapi.json');
    return response.json();
}

export function parseEndpoints(spec: OpenAPISpec) {
    const endpoints = [];

    for (const [path, methods] of Object.entries(spec.paths)) {
        for (const [method, details] of Object.entries(methods)) {
            // @ts-ignore
            endpoints.push({
                path,
                method: method.toUpperCase(),
                ...details
            });
        }
    }

    return endpoints;
}