# 📚 OpenAPI Documentation - KuaiPiao

## ✅ Estado: Production Ready

### Problema Resuelto
El task `./gradlew buildOpenApi` fue optimizado para generar automáticamente especificaciones OpenAPI sin timeout.

### Solución Implementada

#### Generación Automática
- Script Kotlin personalizado: `GenerateOpenApiSpec.kt`
- Genera OpenAPI 3.0.0 en JSON y YAML
- Se ejecuta automáticamente en cada build

#### Gradle Build Integration
- Task: `generateOpenApiSpecFile` (finalizer de buildOpenApi)
- Salida: `/server/src/main/resources/api.json` y `api.yaml`
- Incluye archivos en JAR automáticamente

#### Runtime API
```
GET /openapi.json      → Especificación JSON
GET /docs              → Scalar UI (moderno)
GET /swagger           → Swagger UI (clásico)
```

### Endpoints Documentados

**Authentication** - `/api/v1/oauth`
- POST /sign-up, /sign-in, /logout, /verify-email

**Company** - `/api/v1/company`
- POST /create, GET /find/id/{id}, DELETE /delete/{id}

**Organization** - `/api/v1/org`
- POST /create, GET /find/id/{id}

**Enterprise** - `/api/v1/enterprise`
- POST /create, GET /find/{id}

### Uso

**Generar specs:**
```bash
./gradlew buildOpenApi
```

**Acceder en servidor:**
```
http://localhost:8080/openapi.json
http://localhost:8080/docs
http://localhost:8080/swagger
```

---

## 🚀 Exponer en Frontend (Next.js/Astro)

### Opción A: Servir JSON desde Ktor (Recomendado)

El servidor ya sirve `/openapi.json`. Solo crea el cliente:

**Next.js 14+ (App Router):**
```typescript
// app/api-docs/page.tsx
import { useEffect, useState } from 'react';

export default function ApiDocs() {
  const [spec, setSpec] = useState(null);

  useEffect(() => {
    fetch('http://localhost:8080/openapi.json')
      .then(r => r.json())
      .then(setSpec);
  }, []);

  if (!spec) return <div>Cargando...</div>;

  return (
    <iframe
      src={`https://cdn.jsdelivr.net/npm/@scalar/api-reference@latest/dist/index.html?url=${btoa(JSON.stringify(spec))}`}
      style={{ width: '100%', height: '100vh', border: 'none' }}
    />
  );
}
```

**Astro:**
```astro
---
// src/pages/api-docs.astro
const spec = await fetch('http://localhost:8080/openapi.json')
  .then(r => r.json());
---
<iframe
  src={`https://cdn.jsdelivr.net/npm/@scalar/api-reference@latest/dist/index.html?url=${btoa(JSON.stringify(spec))}`}
  style="width: 100%; height: 100vh; border: none;"
/>
```

### Opción B: Incluir JSON en Frontend

**1. Copiar archivo:**
```bash
cp server/src/main/resources/api.json frontend/public/api.json
```

**2. Next.js:**
```typescript
// app/api-docs/page.tsx
import spec from '@/public/api.json';

export default function ApiDocs() {
  return (
    <iframe
      src={`https://cdn.jsdelivr.net/npm/@scalar/api-reference@latest/dist/index.html?url=${btoa(JSON.stringify(spec))}`}
      style={{ width: '100%', height: '100vh' }}
    />
  );
}
```

**3. Astro:**
```astro
---
import spec from '../public/api.json';
---
<iframe
  src={`https://cdn.jsdelivr.net/npm/@scalar/api-reference@latest/dist/index.html?url=${btoa(JSON.stringify(spec))}`}
  style="width: 100%; height: 100vh;"
/>
```

### Opción C: Componente OpenAPI Interactivo

**Instalar:**
```bash
npm install @scalar/api-reference react
```

**Next.js (RSC compatible):**
```typescript
'use client';
import { ApiReference } from '@scalar/api-reference';
import spec from '@/public/api.json';

export default function ApiDocs() {
  return <ApiReference configuration={{ spec }} />;
}
```

**Astro:**
```astro
---
import { ApiReference } from '@scalar/api-reference';
import spec from '../public/api.json';
---
<ApiReference client:load configuration={{ spec }} />
```

---

## 📋 Resumen de Archivos

| Archivo | Propósito |
|---------|-----------|
| `server/build.gradle.kts` | Task gradle para generar specs |
| `GenerateOpenApiSpec.kt` | Script que genera OpenAPI JSON/YAML |
| `ScalarDocs.kt` | Endpoints para servir documentación |
| `api.json` | Especificación OpenAPI |
| `api.yaml` | Especificación OpenAPI (formato YAML) |

**Archivos MD eliminables:**
- OPENAPI_SETUP.md
- OPENAPI_COMPLETE.md

Este archivo es la referencia única.

---

**Status**: ✅ Production Ready | **Update**: 2025-11-25

