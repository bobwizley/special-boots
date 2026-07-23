---
number: 004
title: Mojang official mappings (Mojmap)
date: 2026-07-22
author: Bob Wizley
status: accepted
---

## Context

O código Java referencia classes do Minecraft cujos nomes dependem da mapping escolhida. A escolha trava toda a base de código e é cara de reverter depois. As duas opções no ecossistema Fabric são Yarn (mappings da comunidade) e Mojmap (mappings oficiais da Mojang).

## Decision

Usar **Mojmap** (Mojang official mappings) via Loom.

## Rationale

- Os dois refs que são mods Java (`special-boots`, `Wool-Tweaks`) já usam nomes Mojmap — a reimplementação de referência flui sem retradução mental.
- Mojmap é o padrão de fato no desenvolvimento Fabric moderno e casa com a decompilação/documentação oficial.

## Considered Alternatives

- **Yarn:** tradicional no Fabric, mas exigiria traduzir os nomes de todos os refs Java; sem ganho que justifique.
