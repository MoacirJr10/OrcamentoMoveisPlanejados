Orçamento Móveis Planejados
Este é um aplicativo Java de desktop para gerenciar orçamentos de móveis planejados. Ele permite cadastrar clientes, itens, criar orçamentos, enviar via WhatsApp, editar orçamentos salvos e deletá-los. A distribuição deste software é restrita e creditada a Moacir Pereira, Engenheiro de Computação (GitHub: MoacirJr10).
Funcionalidades

Cadastro de Clientes: Adicione e gerencie informações de clientes (nome, telefone, endereço).
Cadastro de Itens: Registre itens disponíveis (descrição, preço unitário, dimensão).
Criação de Orçamentos: Crie orçamentos associando clientes e itens com quantidades.
Envio via WhatsApp: Gere e envie orçamentos selecionados diretamente pelo WhatsApp.
Edição de Orçamentos: Modifique orçamentos salvos, adicionando ou removendo itens.
Deleção de Orçamentos: Remova orçamentos salvos permanentemente.
Filtro por Cliente: Filtre orçamentos por cliente para facilitar a visualização.

Pré-requisitos

Sistema Operacional: Windows (outros sistemas podem exigir ajustes).
Banco de Dados: MySQL, PostgreSQL ou outro compatível com JDBC (configuração necessária).

Instalação

Baixe o Executável:
Descompacte o arquivo ZIP fornecido.


Configure o Banco de Dados:
Crie um banco de dados e execute o script SQL em create_tables.sql para criar as tabelas.
Edite o arquivo dao/DatabaseConnection.java (se disponível no código-fonte) ou forneça as credenciais ao iniciar o aplicativo com variáveis de ambiente:
DB_URL=jdbc:mysql://localhost:3306/orcamento_moveis
DB_USER=seu_usuario
DB_PASSWORD=sua_senha




Executar:
Navegue até a pasta descompactada e execute OrcamentoMoveisPlanejados.exe (Windows).
O aplicativo inclui um JRE embutido e exibe um aviso de restrição ao iniciar.



Uso

Iniciar o Aplicativo: Clique no executável para abrir a interface gráfica e veja o aviso de restrição.
Cadastrar Clientes e Itens: Use os botões "Cadastrar Cliente" e "Novo Item" para adicionar dados.
Criar Orçamento: Selecione um cliente, adicione itens e clique em "Salvar Orçamento".
Gerenciar Orçamentos:
Visualize orçamentos salvos na lista "Orçamentos Salvos".
Edite com "Editar Orçamento" ou delete com "Deletar Orçamento".
Envie via WhatsApp selecionando itens e clicando em "Enviar via WhatsApp".


Filtrar: Use o filtro por cliente para listar orçamentos específicos.

Contribuição

Sinta-se à vontade para sugerir melhorias ou relatar problemas, mas a distribuição não autorizada é proibida.
Faça um fork do repositório e envie pull requests (se disponível, com permissão).

Licença
Este software é de uso restrito. Contate o criador (Moacir Pereira, GitHub: MoacirJr10) para detalhes sobre licenciamento.
Contato
Para dúvidas, entre em contato com Moacir Pereira via GitHub: MoacirJr10.
Versão

Versão 1.0 - 28/08/2025, 18:02 (horário de Brasília).
