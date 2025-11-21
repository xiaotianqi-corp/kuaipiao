import { useEffect, useState } from "react";

export default function ScalarDocsViewer() {
    const [spec, setSpec] = useState<any | null>(null);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        async function loadSpec() {
            try {
                const res = await fetch("http://localhost:8080/openapi.json");

                if (!res.ok) {
                    throw new Error(`Failed to fetch spec: ${res.status}`);
                }

                const json = await res.json(); // ✅ debes parsear a JSON
                setSpec(json);
            } catch (err: any) {
                console.error("Error loading OpenAPI spec:", err);
                setError(err.message);
            }
        }

        loadSpec();
    }, []);

    if (error) {
        return (
            <div className="text-red-500 p-4 text-center">
                Error loading OpenAPI spec: {error}
            </div>
        );
    }

    if (!spec) {
        return (
            <div className="text-gray-500 p-4 text-center animate-pulse">
                Loading API documentation...
            </div>
        );
    }

    return (
        <div className="p-6 max-w-5xl mx-auto">
            {/* Header */}
            <h1 className="text-3xl font-bold mb-2">{spec.info?.title || "API Documentation"}</h1>
            <p className="text-gray-600 mb-6">{spec.info?.description || "No description provided."}</p>

            {/* Server info */}
            {spec.servers && spec.servers.length > 0 && (
                <div className="mb-6">
                    <h2 className="text-lg font-semibold text-gray-800">Servers</h2>
                    <ul className="list-disc ml-6 text-gray-700">
                        {spec.servers.map((server: any) => (
                            <li key={server.url}>
                                <span className="font-mono text-blue-700">{server.url}</span>{" "}
                                <span className="text-gray-500">({server.description})</span>
                            </li>
                        ))}
                    </ul>
                </div>
            )}

            {/* Endpoints */}
            <h2 className="text-xl font-semibold mb-4">Endpoints</h2>
            <div className="space-y-6">
                {Object.entries(spec.paths || {}).map(([path, methods]: [string, any]) => (
                    <div key={path} className="border rounded-lg shadow-sm p-4 bg-white">
                        <p className="font-mono text-indigo-700 text-lg">{path}</p>
                        {Object.entries(methods).map(([method, data]: [string, any]) => (
                            <div key={method} className="ml-4 mt-2">
                                <span className="text-sm uppercase font-semibold text-indigo-600">{method}</span>{" "}
                                — <span className="text-gray-800">{data.summary || "No summary"}</span>
                                {data.description && (
                                    <p className="text-gray-500 text-sm ml-1 mt-1">{data.description}</p>
                                )}

                                {/* Request body example */}
                                {data.requestBody && (
                                    <pre className="bg-gray-100 text-sm rounded p-2 mt-2 overflow-x-auto">
                    {JSON.stringify(data.requestBody.content?.["application/json"]?.example, null, 2)}
                  </pre>
                                )}

                                {/* Response example */}
                                {data.responses && data.responses["200"] && (
                                    <pre className="bg-green-50 text-sm rounded p-2 mt-2 overflow-x-auto">
                    {JSON.stringify(
                        data.responses["200"].content?.["application/json"]?.example,
                        null,
                        2
                    )}
                  </pre>
                                )}
                            </div>
                        ))}
                    </div>
                ))}
            </div>
        </div>
    );
}
