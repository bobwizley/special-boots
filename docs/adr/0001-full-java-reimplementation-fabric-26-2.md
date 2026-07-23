---
number: 001
title: Reimplementação total em Java, alvo único Fabric 26.2
date: 2026-07-22
author: Victor Ferreira
status: accepted
---

## Context

O objetivo do projeto é reunir num único mod as funcionalidades espalhadas por 10 projetos de referência em `refs/`. Esses refs são de naturezas heterogêneas: a maioria são datapacks/resource packs puramente declarativos (receitas, worldgen, funções `.mcfunction`), enquanto alguns são mods Fabric com lógica de runtime real (mixins, eventos, encantamentos). Vários trazem suporte a múltiplas versões do Minecraft via overlays (1.20.x até 26.x).

Havia três caminhos possíveis: (A) empacotar os datapacks como recursos embutidos no JAR quase sem código; (B) reimplementar cada funcionalidade em Java idiomático; (C) híbrido.

## Decision

Reimplementar **todas** as funcionalidades selecionadas em Java idiomático, usando o código dos refs apenas como especificação de comportamento — não como fonte para cópia. O conteúdo declarativo (receitas, worldgen) será gerado a partir de código Java e empacotado no JAR.

O alvo é **único**: Minecraft **26.2** sobre **Fabric Loader**. Todo suporte multi-versão dos refs (overlays de versões anteriores) é descartado.

## Rationale

- O foco é reimplementar e **otimizar** as funcionalidades, não replicar datapacks — um mod Java coeso permite lógica, configuração e desempenho que o motor de datapack não oferece.
- Alvo único elimina a complexidade dos overlays de versão dos refs e simplifica mappings, dependências e testes.
- Fabric já é a abordagem do `special-boots`, o único ref que hoje é um mod Fabric puro para versão recente.

## Considered Alternatives

- **Empacotar datapacks no JAR (opção A):** rejeitada. Entregaria rápido, mas contraria o objetivo de reimplementar/otimizar e manteria dependência do motor de datapack para lógica que se beneficia de Java.
