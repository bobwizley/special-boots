---
number: 003
title: Conteúdo declarativo via Fabric Data Generation (datagen)
date: 2026-07-22
author: Bob Wizley
status: accepted
---

## Context

Grande parte do mod é conteúdo declarativo que precisa virar JSON no JAR: centenas de receitas (OmniCut sozinho tem ~400 no ref, More-Buttons, Recycling, cheaper-*, Village-Bell, Wool string), ~40 configured features de árvore (better-trees), definições e tags de enchantment (special-boots). Havia dois caminhos: escrever esses JSON à mão em `resources/` ou gerá-los via Fabric Data Generation a partir de código Java.

## Decision

Usar **Fabric Data Generation**. Providers em Java (recipe providers, worldgen bootstrap, tag providers, etc.) geram os JSON no build via Gradle. O JSON gerado é empacotado no JAR.

## Rationale

- Honra o objetivo de "reimplementar em Java": a fonte de verdade é código Java tipado, não JSON solto.
- As centenas de receitas do OmniCut/More-Buttons/Recycling têm padrões altamente repetitivos (por tipo de madeira, por cor, por material) — loops de datagen eliminam repetição e erro de digitação.
- Worldgen (better-trees) é naturalmente coberto pelo datagen de configured features.
- Regeneração automática mantém o conteúdo consistente a cada mudança.

## Consequences

- É preciso montar o pipeline de datagen (fabric-loom `runDatagen`) logo no início do projeto.
- O conteúdo gerado não deve ser editado à mão; mudanças passam pelos providers.
