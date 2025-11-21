// support/routes.ts
export type Primitive = string | number | boolean | null | undefined
export type QueryValue = Primitive | Primitive[] | Record<string, any>

export type RouteQueryOptions = {
    query?: Record<string, QueryValue>
    mergeQuery?: Record<string, QueryValue>
}

export type RouteDefinitionMethod<M extends string> = {
    url: string
    method: M
}

export type RouteDefinitionMethods<M extends readonly string[]> = {
    methods: M
    url: string
}

export type RouteDefinition<M extends string | readonly string[] = string> = M extends readonly string[]
    ? RouteDefinitionMethods<M>
    : RouteDefinitionMethod<M & string>

export type RouteFormDefinition<M extends string = string> = {
    action: string
    method: M
}

export function applyUrlDefaults<T extends Record<string, any>>(args: T): T {
    return args
}

export function queryParams(options?: RouteQueryOptions): string {
    if (!options) return ''

    let params: Record<string, QueryValue> | undefined
    if (options.mergeQuery) {
        params = options.mergeQuery as Record<string, QueryValue>
    } else if (options.query) {
        params = options.query
    }

    if (!params) return ''

    const usp = new URLSearchParams()

    const appendValue = (key: string, value: QueryValue) => {
        if (value === undefined || value === null) return
        if (Array.isArray(value)) {
            for (const v of value) {
                if (v === undefined || v === null) continue
                usp.append(key, String(v))
            }
            return
        }
        if (typeof value === 'object') {
            try {
                usp.append(key, JSON.stringify(value))
            } catch (e) {
                usp.append(key, String(value))
            }
            return
        }
        usp.append(key, String(value))
    }

    for (const [k, v] of Object.entries(params)) {
        appendValue(k, v as QueryValue)
    }

    const s = usp.toString()
    return s ? `?${s}` : ''
}

export default {
    queryParams,
    applyUrlDefaults,
}