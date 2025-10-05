package view;

import dao.ClienteDAO;
import dao.ItemDAO;
import dao.OrcamentoDAO;
import model.Cliente;
import model.Item;
import model.Orcamento;
import model.OrcamentoItem;
import service.ClienteService;
import service.ItemService;
import service.OrcamentoService;
import service.WhatsappNotificationService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainView extends JFrame {
    private final ClienteService clienteService;
    private final ItemService itemService;
    private final OrcamentoService orcamentoService;
    private final WhatsappNotificationService whatsappService;

    private JComboBox<Cliente> comboClientes;
    private JList<Item> listItensDisponiveis;
    private JList<OrcamentoItem> listItensOrcamento;
    private final DefaultListModel<OrcamentoItem> modelItensOrcamento = new DefaultListModel<>();
    private JTextField txtQuantidade;
    private JButton btnAdicionarItem, btnCriarOrcamento, btnEnviarWhatsApp, btnCadastrarCliente, btnCadastrarItem, btnAjuda;
    private JList<Orcamento> listOrcamentos;
    private final DefaultListModel<Orcamento> modelOrcamentos = new DefaultListModel<>();
    private JButton btnEditarCliente, btnDeletarCliente, btnEditarItem, btnDeletarItem;
    private JComboBox<String> comboFiltroClientes;
    private Orcamento orcamentoEditando = null;

    public MainView() {
        // Instanciação de DAOs e Serviços
        ClienteDAO clienteDAO = new ClienteDAO();
        ItemDAO itemDAO = new ItemDAO();
        OrcamentoDAO orcamentoDAO = new OrcamentoDAO();

        this.clienteService = new ClienteService(clienteDAO);
        this.itemService = new ItemService(itemDAO);
        this.orcamentoService = new OrcamentoService(orcamentoDAO);
        this.whatsappService = new WhatsappNotificationService();

        JOptionPane.showMessageDialog(null,
                "Este programa é de distribuição restrita.\n" +
                        "Criado por: Moacir Pereira\n" +
                        "Engenheiro de Computação\n" +
                        "GitHub: MoacirJr10\n" +
                        "Uso autorizado apenas com permissão do criador.",
                "Aviso de Restrição",
                JOptionPane.INFORMATION_MESSAGE);

        setTitle("Orçamento Móveis Planejados");
        setSize(1100, 700);
        setMinimumSize(new Dimension(900, 600));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(245, 245, 245)); // Fundo claro

        // Configurar estilo global
        UIManager.put("Button.font", new Font("Segoe UI", Font.PLAIN, 14));
        UIManager.put("Label.font", new Font("Segoe UI", Font.PLAIN, 14));
        UIManager.put("TitledBorder.font", new Font("Segoe UI", Font.BOLD, 14));
        UIManager.put("ComboBox.font", new Font("Segoe UI", Font.PLAIN, 14));
        UIManager.put("List.font", new Font("Segoe UI", Font.PLAIN, 14));
        UIManager.put("TextField.font", new Font("Segoe UI", Font.PLAIN, 14));

        // Painel superior: Seleção do cliente
        JPanel panelCliente = new JPanel(new GridBagLayout());
        panelCliente.setBorder(BorderFactory.createCompoundBorder(
                new TitledBorder("Cliente"),
                new EmptyBorder(10, 10, 10, 10)));
        panelCliente.setBackground(new Color(255, 255, 255));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        comboClientes = new JComboBox<>();
        comboClientes.setPreferredSize(new Dimension(300, 30));
        comboClientes.setToolTipText("Selecione um cliente");

        btnCadastrarCliente = createStyledButton("Cadastrar Cliente");
        btnEditarCliente = createStyledButton("Editar Cliente");
        btnDeletarCliente = createStyledButton("Deletar Cliente");

        gbc.gridx = 0;
        gbc.gridy = 0;
        panelCliente.add(new JLabel("Cliente:"), gbc);
        gbc.gridx = 1;
        panelCliente.add(comboClientes, gbc);
        gbc.gridx = 2;
        panelCliente.add(btnCadastrarCliente, gbc);
        gbc.gridx = 3;
        panelCliente.add(btnEditarCliente, gbc);
        gbc.gridx = 4;
        panelCliente.add(btnDeletarCliente, gbc);
        add(panelCliente, BorderLayout.NORTH);

        // Painéis centrais com SplitPane
        JSplitPane splitMain = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitMain.setResizeWeight(0.4);
        splitMain.setDividerSize(8);
        splitMain.setBackground(new Color(200, 200, 200));

        // Painel Itens disponíveis
        JPanel panelItens = new JPanel(new BorderLayout(5, 5));
        panelItens.setBorder(BorderFactory.createCompoundBorder(
                new TitledBorder("Itens Disponíveis"),
                new EmptyBorder(5, 5, 5, 5)));
        panelItens.setBackground(new Color(255, 255, 255));
        listItensDisponiveis = new JList<>();
        listItensDisponiveis.setVisibleRowCount(15);
        listItensDisponiveis.setSelectionBackground(new Color(200, 220, 255));
        JScrollPane scrollDisponiveis = new JScrollPane(listItensDisponiveis);
        panelItens.add(scrollDisponiveis, BorderLayout.CENTER);

        JPanel panelItensButtons = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panelItensButtons.setBackground(new Color(255, 255, 255));
        btnEditarItem = createStyledButton("Editar Item");
        btnDeletarItem = createStyledButton("Deletar Item");
        panelItensButtons.add(btnEditarItem);
        panelItensButtons.add(btnDeletarItem);
        panelItens.add(panelItensButtons, BorderLayout.SOUTH);

        // Painel Orçamento atual
        JPanel panelOrcamento = new JPanel(new BorderLayout(5, 5));
        panelOrcamento.setBorder(BorderFactory.createCompoundBorder(
                new TitledBorder("Itens no Orçamento"),
                new EmptyBorder(5, 5, 5, 5)));
        panelOrcamento.setBackground(new Color(255, 255, 255));
        listItensOrcamento = new JList<>(modelItensOrcamento);
        listItensOrcamento.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        listItensOrcamento.setSelectionBackground(new Color(200, 220, 255));
        JScrollPane scrollOrcamento = new JScrollPane(listItensOrcamento);
        panelOrcamento.add(scrollOrcamento, BorderLayout.CENTER);

        JPanel panelAdd = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panelAdd.setBackground(new Color(255, 255, 255));
        txtQuantidade = new JTextField(5);
        txtQuantidade.setToolTipText("Digite a quantidade do item");
        btnAdicionarItem = createStyledButton("Adicionar Item");
        btnCadastrarItem = createStyledButton("Novo Item");
        panelAdd.add(new JLabel("Quantidade:"));
        panelAdd.add(txtQuantidade);
        panelAdd.add(btnAdicionarItem);
        panelAdd.add(btnCadastrarItem);
        panelOrcamento.add(panelAdd, BorderLayout.SOUTH);

        // Painel Orçamentos salvos
        JPanel panelOrcamentos = new JPanel(new BorderLayout(5, 5));
        panelOrcamentos.setBorder(BorderFactory.createCompoundBorder(
                new TitledBorder("Orçamentos Salvos"),
                new EmptyBorder(5, 5, 5, 5)));
        panelOrcamentos.setBackground(new Color(255, 255, 255));
        listOrcamentos = new JList<>(modelOrcamentos);
        listOrcamentos.setSelectionBackground(new Color(200, 220, 255));
        JScrollPane scrollOrcamentos = new JScrollPane(listOrcamentos);
        panelOrcamentos.add(scrollOrcamentos, BorderLayout.CENTER);

        // Filtro de orçamentos
        JPanel panelFiltro = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panelFiltro.setBackground(new Color(255, 255, 255));
        comboFiltroClientes = new JComboBox<>();
        comboFiltroClientes.addItem("Todos");
        comboFiltroClientes.setToolTipText("Filtrar orçamentos por cliente");
        panelFiltro.add(new JLabel("Filtrar por Cliente:"));
        panelFiltro.add(comboFiltroClientes);

        // Botões de edição e deleção
        JPanel panelAcoesOrcamento = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panelAcoesOrcamento.setBackground(new Color(255, 255, 255));
        JButton btnEditarOrcamento = createStyledButton("Editar Orçamento");
        JButton btnDeletarOrcamento = createStyledButton("Deletar Orçamento");
        btnEditarOrcamento.addActionListener(e -> editarOrcamentoSelecionado());
        btnDeletarOrcamento.addActionListener(e -> deletarOrcamentoSelecionado());
        panelAcoesOrcamento.add(btnEditarOrcamento);
        panelAcoesOrcamento.add(btnDeletarOrcamento);
        panelOrcamentos.add(panelAcoesOrcamento, BorderLayout.SOUTH);
        panelOrcamentos.add(panelFiltro, BorderLayout.NORTH);

        // Montar o split central
        JSplitPane splitRight = new JSplitPane(JSplitPane.VERTICAL_SPLIT, panelOrcamento, panelOrcamentos);
        splitRight.setResizeWeight(0.5);
        splitRight.setDividerSize(8);
        splitMain.setLeftComponent(panelItens);
        splitMain.setRightComponent(splitRight);
        add(splitMain, BorderLayout.CENTER);

        // Painel inferior: Botões de ação
        JPanel panelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panelBotoes.setBackground(new Color(255, 255, 255));
        btnCriarOrcamento = createStyledButton("Salvar Orçamento");
        btnEnviarWhatsApp = createStyledButton("Enviar via WhatsApp");
        btnAjuda = createStyledButton("Ajuda");
        panelBotoes.add(btnCriarOrcamento);
        panelBotoes.add(btnEnviarWhatsApp);
        panelBotoes.add(btnAjuda);
        add(panelBotoes, BorderLayout.SOUTH);

        // Eventos
        btnCadastrarCliente.addActionListener(e -> cadastrarCliente());
        btnEditarCliente.addActionListener(e -> editarCliente());
        btnDeletarCliente.addActionListener(e -> deletarCliente());
        btnCadastrarItem.addActionListener(e -> cadastrarItem());
        btnEditarItem.addActionListener(e -> editarItem());
        btnDeletarItem.addActionListener(e -> deletarItem());
        btnAdicionarItem.addActionListener(e -> adicionarItemAoOrcamento());
        btnCriarOrcamento.addActionListener(e -> salvarOrcamento());
        btnEnviarWhatsApp.addActionListener(e -> enviarOrcamentoSelecionado());
        comboFiltroClientes.addActionListener(e -> filtrarOrcamentos());
        btnAjuda.addActionListener(e -> mostrarAjuda());

        // Ouvinte para sincronizar itens ao selecionar um orçamento
        listOrcamentos.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    Orcamento selected = listOrcamentos.getSelectedValue();
                    if (selected != null) {
                        try {
                            List<OrcamentoItem> itens = orcamentoService.carregarItensOrcamento(selected.getId());
                            modelItensOrcamento.clear();
                            for (OrcamentoItem item : itens) {
                                modelItensOrcamento.addElement(item);
                            }
                        } catch (SQLException ex) {
                            JOptionPane.showMessageDialog(MainView.this, "Erro ao carregar itens: " + ex.getMessage());
                        }
                    }
                }
            }
        });

        carregarDados();
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(new Color(66, 135, 245));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void carregarDados() {
        try {
            // Carregar clientes
            comboClientes.removeAllItems();
            comboFiltroClientes.removeAllItems();
            comboFiltroClientes.addItem("Todos");
            List<Cliente> clientes = clienteService.listarClientes();
            for (Cliente c : clientes) {
                comboClientes.addItem(c);
                comboFiltroClientes.addItem(c.toString());
            }

            // Carregar itens
            DefaultListModel<Item> modelItens = new DefaultListModel<>();
            for (Item i : itemService.listarItens()) {
                modelItens.addElement(i);
            }
            listItensDisponiveis.setModel(modelItens);

            // Carregar orçamentos
            filtrarOrcamentos();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar dados: " + e.getMessage());
        }
    }

    private void filtrarOrcamentos() {
        try {
            String selectedFiltro = (String) comboFiltroClientes.getSelectedItem();
            List<Orcamento> todosOrcamentos = orcamentoService.listarOrcamentos();
            modelOrcamentos.clear();
            if ("Todos".equals(selectedFiltro)) {
                for (Orcamento o : todosOrcamentos) {
                    modelOrcamentos.addElement(o);
                }
            } else {
                for (Orcamento o : todosOrcamentos) {
                    if (o.getCliente().toString().equals(selectedFiltro)) {
                        modelOrcamentos.addElement(o);
                    }
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao filtrar: " + e.getMessage());
        }
    }

    private void cadastrarCliente() {
        String nome = JOptionPane.showInputDialog("Nome:");
        String telefone = JOptionPane.showInputDialog("Telefone (ex: +5511999999999):");
        String endereco = JOptionPane.showInputDialog("Endereço:");
        if (nome != null && telefone != null && endereco != null) {
            try {
                Cliente cliente = new Cliente(nome, telefone, endereco);
                clienteService.cadastrarCliente(cliente);
                carregarDados();
            } catch (IllegalArgumentException | SQLException e) {
                JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage());
            }
        }
    }

    private void editarCliente() {
        Cliente selected = (Cliente) comboClientes.getSelectedItem();
        if (selected != null) {
            String novoNome = JOptionPane.showInputDialog("Novo Nome:", selected.getNome());
            String novoTelefone = JOptionPane.showInputDialog("Novo Telefone:", selected.getTelefone());
            String novoEndereco = JOptionPane.showInputDialog("Novo Endereço:", selected.getEndereco());
            try {
                selected.setNome(novoNome);
                selected.setTelefone(novoTelefone);
                selected.setEndereco(novoEndereco);
                clienteService.atualizarCliente(selected);
                carregarDados();
            } catch (IllegalArgumentException | SQLException e) {
                JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage());
            }
        }
    }

    private void deletarCliente() {
        Cliente selected = (Cliente) comboClientes.getSelectedItem();
        if (selected != null && JOptionPane.showConfirmDialog(this, "Deletar cliente " + selected.getNome() + "?", "Confirmação", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try {
                clienteService.deletarCliente(selected.getId());
                carregarDados();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage());
            }
        }
    }

    private void cadastrarItem() {
        String descricao = JOptionPane.showInputDialog("Descrição:");
        String precoStr = JOptionPane.showInputDialog("Preço Unitário:");
        String dimensao = JOptionPane.showInputDialog("Dimensão (ex: 2m x 1m):");
        if (descricao != null && precoStr != null) {
            try {
                double preco = Double.parseDouble(precoStr);
                if (descricao.trim().isEmpty()) {
                    throw new IllegalArgumentException("Descrição não pode ser vazia.");
                }
                itemService.cadastrarItem(new Item(descricao.trim(), preco, dimensao));
                carregarDados();
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Erro: Preço deve ser um número válido.");
            } catch (IllegalArgumentException e) {
                JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage());
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Erro ao acessar o banco de dados: " + e.getMessage());
            }
        }
    }

    private void editarItem() {
        Item selected = listItensDisponiveis.getSelectedValue();
        if (selected != null) {
            String novaDescricao = JOptionPane.showInputDialog("Nova Descrição:", selected.getDescricao());
            String novoPrecoStr = JOptionPane.showInputDialog("Novo Preço:", selected.getPrecoUnitario());
            String novaDimensao = JOptionPane.showInputDialog("Nova Dimensão:", selected.getDimensao());
            try {
                double novoPreco = Double.parseDouble(novoPrecoStr);
                selected.setDescricao(novaDescricao);
                selected.setPrecoUnitario(novoPreco);
                selected.setDimensao(novaDimensao);
                itemService.atualizarItem(selected);
                carregarDados();
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Erro: Preço deve ser um número válido.");
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage());
            }
        }
    }

    private void deletarItem() {
        Item selected = listItensDisponiveis.getSelectedValue();
        if (selected != null && JOptionPane.showConfirmDialog(this, "Deletar item " + selected.getDescricao() + "?", "Confirmação", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try {
                itemService.deletarItem(selected.getId());
                carregarDados();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage());
            }
        }
    }

    private void adicionarItemAoOrcamento() {
        Item selected = listItensDisponiveis.getSelectedValue();
        if (selected != null && !txtQuantidade.getText().isEmpty()) {
            try {
                int qtd = Integer.parseInt(txtQuantidade.getText());
                if (qtd <= 0) {
                    throw new IllegalArgumentException("Quantidade deve ser positiva.");
                }
                if (orcamentoEditando != null) {
                    orcamentoEditando.adicionarItem(selected, qtd);
                    atualizarModelItensOrcamento();
                } else {
                    modelItensOrcamento.addElement(new OrcamentoItem(selected, qtd));
                }
                txtQuantidade.setText("");
            } catch (IllegalArgumentException e) {
                JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage());
            }
        }
    }

    private void salvarOrcamento() {
        Cliente cliente = (Cliente) comboClientes.getSelectedItem();
        if (cliente != null && modelItensOrcamento.size() > 0) {
            Orcamento orc = new Orcamento(cliente, new Date());
            for (int i = 0; i < modelItensOrcamento.size(); i++) {
                orc.adicionarItem(modelItensOrcamento.get(i).getItem(), modelItensOrcamento.get(i).getQuantidade());
            }
            try {
                orcamentoService.criarOrcamento(orc);
                JOptionPane.showMessageDialog(this, "Orçamento salvo!");
                modelItensOrcamento.clear();
                carregarDados();
                orcamentoEditando = null;
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(this, "Selecione um cliente e adicione itens.");
        }
    }

    private void enviarOrcamentoSelecionado() {
        Orcamento selected = listOrcamentos.getSelectedValue();
        if (selected != null) {
            try {
                List<OrcamentoItem> itensOrcamento = orcamentoService.carregarItensOrcamento(selected.getId());
                modelItensOrcamento.clear();
                for (OrcamentoItem item : itensOrcamento) {
                    modelItensOrcamento.addElement(item);
                }

                int[] selectedIndices = listItensOrcamento.getSelectedIndices();
                List<OrcamentoItem> itensSelecionados = new ArrayList<>();
                if (selectedIndices.length > 0) {
                    for (int index : selectedIndices) {
                        itensSelecionados.add(modelItensOrcamento.getElementAt(index));
                    }
                } else {
                    itensSelecionados.addAll(itensOrcamento);
                }

                Orcamento orcamentoFiltrado = new Orcamento(selected.getCliente(), selected.getData());
                orcamentoFiltrado.setId(selected.getId());
                orcamentoFiltrado.setTotal(calcularTotalItens(itensSelecionados));
                orcamentoFiltrado.getItens().addAll(itensSelecionados);

                String mensagem = whatsappService.gerarMensagemOrcamento(orcamentoFiltrado);
                int resposta = JOptionPane.showConfirmDialog(this,
                        "Pré-visualização da mensagem:\n\n" + mensagem + "\n\nDeseja enviar esta mensagem?",
                        "Pré-visualização do Orçamento",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.INFORMATION_MESSAGE);

                if (resposta == JOptionPane.YES_OPTION) {
                    try {
                        whatsappService.enviarOrcamento(orcamentoFiltrado);
                        JOptionPane.showMessageDialog(this, "Orçamento enviado com sucesso!");
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(this, "Erro ao enviar o orçamento: " + e.getMessage());
                    }
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Erro ao carregar itens do orçamento: " + e.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(this, "Selecione um orçamento salvo na lista.");
        }
    }

    private void editarOrcamentoSelecionado() {
        Orcamento selected = listOrcamentos.getSelectedValue();
        if (selected != null) {
            try {
                orcamentoEditando = new Orcamento(selected.getCliente(), selected.getData());
                orcamentoEditando.setId(selected.getId());
                List<OrcamentoItem> itens = orcamentoService.carregarItensOrcamento(selected.getId());
                for (OrcamentoItem item : itens) {
                    orcamentoEditando.adicionarItem(item.getItem(), item.getQuantidade());
                }

                JDialog editDialog = new JDialog(this, "Editar Orçamento", true);
                editDialog.setLayout(new BorderLayout(10, 10));
                editDialog.setSize(450, 350);
                editDialog.setBackground(new Color(255, 255, 255));

                JLabel labelInfo = new JLabel("Cliente: " + selected.getCliente().getNome() + " | Data: " + new java.text.SimpleDateFormat("dd-MM-yyyy").format(selected.getData()));
                labelInfo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                editDialog.add(labelInfo, BorderLayout.NORTH);

                JList<OrcamentoItem> listEditItens = new JList<>(modelItensOrcamento);
                listEditItens.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
                listEditItens.setSelectionBackground(new Color(200, 220, 255));
                JScrollPane scrollEdit = new JScrollPane(listEditItens);
                editDialog.add(scrollEdit, BorderLayout.CENTER);

                JPanel panelButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
                panelButtons.setBackground(new Color(255, 255, 255));
                JButton btnRemover = createStyledButton("Remover Itens");
                JButton btnAdicionarNovo = createStyledButton("Adicionar Item");
                JButton btnSalvar = createStyledButton("Salvar Alterações");
                JButton btnCancelar = createStyledButton("Cancelar");

                btnRemover.addActionListener(e -> {
                    int[] indices = listEditItens.getSelectedIndices();
                    for (int i = indices.length - 1; i >= 0; i--) {
                        modelItensOrcamento.remove(indices[i]);
                        orcamentoEditando.getItens().remove(indices[i]);
                    }
                    orcamentoEditando.setTotal(calcularTotalItens(new ArrayList<>(orcamentoEditando.getItens())));
                });

                btnAdicionarNovo.addActionListener(e -> adicionarItemAoOrcamento());

                btnSalvar.addActionListener(e -> {
                    try {
                        orcamentoService.atualizarOrcamento(orcamentoEditando);
                        JOptionPane.showMessageDialog(editDialog, "Orçamento atualizado com sucesso!");
                        carregarDados();
                        editDialog.dispose();
                        orcamentoEditando = null;
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(editDialog, "Erro ao salvar: " + ex.getMessage());
                    }
                });

                btnCancelar.addActionListener(e -> {
                    editDialog.dispose();
                    orcamentoEditando = null;
                });

                panelButtons.add(btnRemover);
                panelButtons.add(btnAdicionarNovo);
                panelButtons.add(btnSalvar);
                panelButtons.add(btnCancelar);
                editDialog.add(panelButtons, BorderLayout.SOUTH);

                atualizarModelItensOrcamento();
                editDialog.setLocationRelativeTo(this);
                editDialog.setVisible(true);
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Erro ao carregar itens para edição: " + e.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(this, "Selecione um orçamento para editar.");
        }
    }

    private void deletarOrcamentoSelecionado() {
        Orcamento selected = listOrcamentos.getSelectedValue();
        if (selected != null) {
            int resposta = JOptionPane.showConfirmDialog(this,
                    "Tem certeza que deseja deletar o orçamento de " + selected.getCliente().getNome() +
                            " (Data: " + new java.text.SimpleDateFormat("dd-MM-yyyy").format(selected.getData()) + ")?",
                    "Confirmação de Deleção",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
            if (resposta == JOptionPane.YES_OPTION) {
                try {
                    orcamentoService.deletarOrcamento(selected.getId());
                    JOptionPane.showMessageDialog(this, "Orçamento deletado com sucesso!");
                    carregarDados();
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(this, "Erro ao deletar o orçamento: " + e.getMessage());
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Selecione um orçamento para deletar.");
        }
    }

    private void atualizarModelItensOrcamento() {
        modelItensOrcamento.clear();
        if (orcamentoEditando != null) {
            for (OrcamentoItem item : orcamentoEditando.getItens()) {
                modelItensOrcamento.addElement(item);
            }
        }
    }

    private double calcularTotalItens(List<OrcamentoItem> itens) {
        double total = 0.0;
        for (OrcamentoItem item : itens) {
            total += item.getItem().getPrecoUnitario() * item.getQuantidade();
        }
        return total;
    }

    private void mostrarAjuda() {
        String ajudaHtml = "<html>" +
                "<body style='width: 400px; font-family: Segoe UI, sans-serif; font-size: 12px;'>" +
                "<h1>Guia de Ajuda</h1>" +
                "<h2>Gerenciamento de Clientes</h2>" +
                "<p><b>Cadastrar:</b> Clique em 'Cadastrar Cliente' para adicionar um novo cliente.</p>" +
                "<p><b>Editar:</b> Selecione um cliente na lista suspensa e clique em 'Editar Cliente'.</p>" +
                "<p><b>Deletar:</b> Selecione um cliente e clique em 'Deletar Cliente'.</p>" +
                "<hr>" +
                "<h2>Gerenciamento de Itens</h2>" +
                "<p><b>Listagem:</b> Os itens cadastrados no sistema aparecem em 'Itens Disponíveis'.</p>" +
                "<p><b>Cadastrar:</b> Clique em 'Novo Item' (abaixo da lista de itens do orçamento) para adicionar um novo tipo de item ao sistema.</p>" +
                "<p><b>Editar/Deletar:</b> Selecione um item na lista 'Itens Disponíveis' e use os botões 'Editar Item' ou 'Deletar Item' abaixo dela.</p>" +
                "<hr>" +
                "<h2>Criação de Orçamentos</h2>" +
                "<ol>" +
                "<li>Selecione um cliente na lista suspensa no topo.</li>" +
                "<li>Na lista 'Itens Disponíveis', selecione o item desejado.</li>" +
                "<li>Digite a quantidade no campo 'Quantidade'.</li>" +
                "<li>Clique em 'Adicionar Item' para incluí-lo no orçamento atual.</li>" +
                "<li>Repita para todos os itens desejados.</li>" +
                "<li>Clique em 'Salvar Orçamento' para gravar.</li>" +
                "</ol>" +
                "<hr>" +
                "<h2>Orçamentos Salvos</h2>" +
                "<p><b>Visualizar:</b> A lista 'Orçamentos Salvos' mostra todos os orçamentos. Use o filtro para ver orçamentos de um cliente específico.</p>" +
                "<p><b>Carregar Itens:</b> Clique em um orçamento na lista para ver os itens que o compõem na lista 'Itens no Orçamento'.</p>" +
                "<p><b>Editar:</b> Selecione um orçamento e clique em 'Editar Orçamento'. Uma nova janela permitirá adicionar ou remover itens.</p>" +
                "<p><b>Deletar:</b> Selecione um orçamento e clique em 'Deletar Orçamento'.</p>" +
                "<p><b>Enviar via WhatsApp:</b> Selecione um orçamento e clique em 'Enviar via WhatsApp'. Uma pré-visualização será exibida antes do envio.</p>" +
                "</body>" +
                "</html>";

        JOptionPane.showMessageDialog(this, new JLabel(ajudaHtml), "Ajuda", JOptionPane.INFORMATION_MESSAGE);
    }
}
