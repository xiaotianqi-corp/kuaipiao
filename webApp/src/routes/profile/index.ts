import { queryParams, type RouteQueryOptions, type RouteDefinition, type RouteFormDefinition } from '@/support/routes.ts'
/**
 * @generated route (source reference removed)
 * @route '/settings/profile'
 */
export const edit = (options?: RouteQueryOptions): RouteDefinition<'get'> => ({
    url: edit.url(options),
    method: 'get',
})

edit.definition = {
    methods: ["get","head"],
    url: '/settings/profile',
} satisfies RouteDefinition<["get","head"]>

/**
 * @generated route (source reference removed)
 * @route '/settings/profile'
 */
edit.url = (options?: RouteQueryOptions) => {
    return edit.definition.url + queryParams(options)
}

/**
 * @generated route (source reference removed)
 * @route '/settings/profile'
 */
edit.get = (options?: RouteQueryOptions): RouteDefinition<'get'> => ({
    url: edit.url(options),
    method: 'get',
})

/**
 * @generated route (source reference removed)
 * @route '/settings/profile'
 */
edit.head = (options?: RouteQueryOptions): RouteDefinition<'head'> => ({
    url: edit.url(options),
    method: 'head',
})

/**
 * @generated route (source reference removed)
 * @route '/settings/profile'
 */
const editForm = (options?: RouteQueryOptions): RouteFormDefinition<'get'> => ({
    action: edit.url(options),
    method: 'get',
})

/**
 * @generated route (source reference removed)
 * @route '/settings/profile'
 */
editForm.get = (options?: RouteQueryOptions): RouteFormDefinition<'get'> => ({
    action: edit.url(options),
    method: 'get',
})

/**
 * @generated route (source reference removed)
 * @route '/settings/profile'
 */
editForm.head = (options?: RouteQueryOptions): RouteFormDefinition<'get'> => ({
    action: edit.url({
        [options?.mergeQuery ? 'mergeQuery' : 'query']: {
            _method: 'HEAD',
            ...(options?.query ?? options?.mergeQuery ?? {}),
        }
    }),
    method: 'get',
})

edit.form = editForm

/**
 * @generated route (source reference removed)
 * @route '/settings/profile'
 */
export const update = (options?: RouteQueryOptions): RouteDefinition<'patch'> => ({
    url: update.url(options),
    method: 'patch',
})

update.definition = {
    methods: ["patch"],
    url: '/settings/profile',
} satisfies RouteDefinition<["patch"]>

/**
 * @generated route (source reference removed)
 * @route '/settings/profile'
 */
update.url = (options?: RouteQueryOptions) => {
    return update.definition.url + queryParams(options)
}

/**
 * @generated route (source reference removed)
 * @route '/settings/profile'
 */
update.patch = (options?: RouteQueryOptions): RouteDefinition<'patch'> => ({
    url: update.url(options),
    method: 'patch',
})

/**
 * @generated route (source reference removed)
 * @route '/settings/profile'
 */
const updateForm = (options?: RouteQueryOptions): RouteFormDefinition<'post'> => ({
    action: update.url({
        [options?.mergeQuery ? 'mergeQuery' : 'query']: {
            _method: 'PATCH',
            ...(options?.query ?? options?.mergeQuery ?? {}),
        }
    }),
    method: 'post',
})

/**
 * @generated route (source reference removed)
 * @route '/settings/profile'
 */
updateForm.patch = (options?: RouteQueryOptions): RouteFormDefinition<'post'> => ({
    action: update.url({
        [options?.mergeQuery ? 'mergeQuery' : 'query']: {
            _method: 'PATCH',
            ...(options?.query ?? options?.mergeQuery ?? {}),
        }
    }),
    method: 'post',
})

update.form = updateForm

/**
 * @generated route (source reference removed)
 * @route '/settings/profile'
 */
export const destroy = (options?: RouteQueryOptions): RouteDefinition<'delete'> => ({
    url: destroy.url(options),
    method: 'delete',
})

destroy.definition = {
    methods: ["delete"],
    url: '/settings/profile',
} satisfies RouteDefinition<["delete"]>

/**
 * @generated route (source reference removed)
 * @route '/settings/profile'
 */
destroy.url = (options?: RouteQueryOptions) => {
    return destroy.definition.url + queryParams(options)
}

/**
 * @generated route (source reference removed)
 * @route '/settings/profile'
 */
destroy.delete = (options?: RouteQueryOptions): RouteDefinition<'delete'> => ({
    url: destroy.url(options),
    method: 'delete',
})

/**
 * @generated route (source reference removed)
 * @route '/settings/profile'
 */
const destroyForm = (options?: RouteQueryOptions): RouteFormDefinition<'post'> => ({
    action: destroy.url({
        [options?.mergeQuery ? 'mergeQuery' : 'query']: {
            _method: 'DELETE',
            ...(options?.query ?? options?.mergeQuery ?? {}),
        }
    }),
    method: 'post',
})

/**
 * @generated route (source reference removed)
 * @route '/settings/profile'
 */
destroyForm.delete = (options?: RouteQueryOptions): RouteFormDefinition<'post'> => ({
    action: destroy.url({
        [options?.mergeQuery ? 'mergeQuery' : 'query']: {
            _method: 'DELETE',
            ...(options?.query ?? options?.mergeQuery ?? {}),
        }
    }),
    method: 'post',
})

destroy.form = destroyForm

const profile = {
    edit,
    update,
    destroy,
}

export default profile