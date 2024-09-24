# Atividade em Grupo da disciplina de Programação Concorrente Distribuída

## Descrição

Este repositório contém os arquivos desenvolvido para a resolução da atividade coletiva da disciplina de Programação Concorrente e Distribuída. A atividade consiste na criação de um experimento para comparar a performance da leitura de alguns arquivos de acordo com o número de threads usado.

## Sobre o desenvolvimento

O projeto foi desenvolvido na IDE **VScode**. A versão do **Java** utilizada foi a **JDK 17**.

## Como Executar

Para executar primeiramente é necessário conferir a versão do Java instalada. Essa versão deve ser a 17, utilizada para o desenvolvimento desse projeto.

Para o correto funcionamento da IDE as extensões **Java**, da Oracle Corporation e o **Extension Pack for Java**, da Microsoft. Ambas estão disponíveis para download na plataforma.

Após o download dos arquivos do projeto, o usuário deve localizar a pasta **temperaturas_cidades** que contém os arquivos csv a serem lidos. Após isso o paramêtro pathname deve ser alterado nas funções runExperiment e runYearExperiment nas linhas 17 e 174, respectivamente, da classe Experiment.java.



´´´
public void runExperiment() {
       File directory = new File("path da pasta temperatura_cidades");
        ....}


private void runYearExperiment() {
        File directory = new File("path da pasta temperatura_cidades");
        ....}

´´´
