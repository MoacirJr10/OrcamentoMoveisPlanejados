package view;

import dao.OrcamentoDAO;
import model.Cliente;
import model.Item;
import model.Orcamento;
import model.OrcamentoItem;
import service.OrcamentoService;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainView extends JFrame {
    private final OrcamentoService service = new OrcamentoService();
    private final OrcamentoDAO orcamentoDAO = new OrcamentoDAO(); // Adicionado para carregar e salvar itens

    private JComboBox<Cliente> comboClientes;
    private JList<Item> listItensDisponiveis;
    private JList<OrcamentoItem> listItensOrcamento;
    private final DefaultListModel<OrcamentoItem> modelItensOrcamento = new DefaultListModel<>();
    private JTextField txtQuantidade;
    private JButton btnAdicionarItem, btnCriarOrcamento, btnEnviarWhatsApp, btnCadastrarCliente, btnCadastrarItem;
    private JList<Orcamento> listOrcamentos;
    private final DefaultListModel<Orcamento> modelOrcamentos = new DefaultListModel<>();
    private JButton btnEditarCliente, btnDeletarCliente, btnEditarItem, btnDeletarItem;
    private JComboBox<String> comboFiltroClientes; // Filter budgets by client
    private Orcamento orcamentoEditando = null; // Para rastrear o orçamento em edição

    public MainView() {
        // Exibir aviso de restrição com crédito ao criador
        JOptionPane.showMessageDialog(null,
                "Este programa é de distribuição restrita.\n" +
                        "Criado por: Moacir Pereira" +
                        " Engenheiro de Computação\n" +
                        "GitHub: MoacirJr10\n" +
                        "Uso autorizado apenas com permissão do criador.",
                "Aviso de Restrição",
                JOptionPane.INFORMATION_MESSAGE);

        setTitle("Orçamento Móveis Planejados");
        setSize(1000, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centraliza a janela
        setLayout(new BorderLayout(10, 10));

        // Painel superior: Seleção do cliente
        JPanel panelCliente = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panelCliente.setBorder(new TitledBorder("Cliente"));
        comboClientes = new JComboBox<>();
        comboClientes.setPreferredSize(new Dimension(250, 25));
        btnCadastrarCliente = new JButton("Cadastrar Cliente");
        btnEditarCliente = new JButton("Editar Cliente");
        btnDeletarCliente = new JButton("Deletar Cliente");
        panelCliente.add(new JLabel("Selecionar:"));
        panelCliente.add(comboClientes);
        panelCliente.add(btnCadastrarCliente);
        panelCliente.add(btnEditarCliente);
        panelCliente.add(btnDeletarCliente);
        add(panelCliente, BorderLayout.NORTH);

        // Painéis centrais com SplitPane (responsivo)
        JSplitPane splitMain = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitMain.setResizeWeight(0.33); // divide em 1/3 - 2/3

        // Painel Itens disponíveis
        JPanel panelItens = new JPanel(new BorderLayout(5, 5));
        panelItens.setBorder(new TitledBorder("Itens Disponíveis"));
        listItensDisponiveis = new JList<>();
        listItensDisponiveis.setVisibleRowCount(10);
        JScrollPane scrollDisponiveis = new JScrollPane(listItensDisponiveis);
        panelItens.add(scrollDisponiveis, BorderLayout.CENTER);
        btnEditarItem = new JButton("Editar Item");
        btnDeletarItem = new JButton("Deletar Item");
        JPanel panelItensButtons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelItensButtons.add(btnEditarItem);
        panelItensButtons.add(btnDeletarItem);
        panelItens.add(panelItensButtons, BorderLayout.SOUTH);

        // Painel Orçamento atual
        JPanel panelOrcamento = new JPanel(new BorderLayout(5, 5));
        panelOrcamento.setBorder(new TitledBorder("Itens no Orçamento"));
        listItensOrcamento = new JList<>(modelItensOrcamento);
        listItensOrcamento.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION); // Habilitar seleção múltipla
        JScrollPane scrollOrcamento = new JScrollPane(listItensOrcamento);
        panelOrcamento.add(scrollOrcamento, BorderLayout.CENTER);

        JPanel panelAdd = new JPanel(new FlowLayout(FlowLayout.LEFT));
        txtQuantidade = new JTextField(5);
        txtQuantidade.setToolTipText("Digite a quantidade");
        btnAdicionarItem = new JButton("Adicionar");
        btnCadastrarItem = new JButton("Novo Item");
        panelAdd.add(new JLabel("Qtd:"));
        panelAdd.add(txtQuantidade);
        panelAdd.add(btnAdicionarItem);
        panelAdd.add(btnCadastrarItem);
        panelOrcamento.add(panelAdd, BorderLayout.SOUTH);

        // Painel Orçamentos salvos
        JPanel panelOrcamentos = new JPanel(new BorderLayout(5, 5));
        panelOrcamentos.setBorder(new TitledBorder("Orçamentos Salvos"));
        listOrcamentos = new JList<>(modelOrcamentos);
        JScrollPane scrollOrcamentos = new JScrollPane(listOrcamentos);
        panelOrcamentos.add(scrollOrcamentos, BorderLayout.CENTER);

        // Adicionar filtro para orçamentos por cliente
        comboFiltroClientes = new JComboBox<>();
        comboFiltroClientes.addItem("Todos");
        JPanel panelFiltro = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelFiltro.add(new JLabel("Filtrar por Cliente:"));
        panelFiltro.add(comboFiltroClientes);
        panelOrcamentos.add(panelFiltro, BorderLayout.SOUTH);

        // Botões de edição e deleção
        JPanel panelAcoesOrcamento = new JPanel(new FlowLayout());
        JButton btnEditarOrcamento = new JButton("Editar Orçamento");
        JButton btnDeletarOrcamento = new JButton("Deletar Orçamento");
        btnEditarOrcamento.addActionListener(e -> editarOrcamentoSelecionado());
        btnDeletarOrcamento.addActionListener(e -> deletarOrcamentoSelecionado());
        panelAcoesOrcamento.add(btnEditarOrcamento);
        panelAcoesOrcamento.add(btnDeletarOrcamento);
        panelOrcamentos.add(panelAcoesOrcamento, BorderLayout.SOUTH);

        // Montar o split central
        JSplitPane splitRight = new JSplitPane(JSplitPane.VERTICAL_SPLIT, panelOrcamento, panelOrcamentos);
        splitRight.setResizeWeight(0.5);

        splitMain.setLeftComponent(panelItens);
        splitMain.setRightComponent(splitRight);

        add(splitMain, BorderLayout.CENTER);

        // Painel inferior: Botões de ação
        JPanel panelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        btnCriarOrcamento = new JButton("Salvar Orçamento");
        btnEnviarWhatsApp = new JButton("Enviar via WhatsApp");
        panelBotoes.add(btnCriarOrcamento);
        panelBotoes.add(btnEnviarWhatsApp);
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

        // Ouvinte para sincronizar itens ao selecionar um orçamento
        listOrcamentos.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    Orcamento selected = listOrcamentos.getSelectedValue();
                    if (selected != null) {
                        try {
                            List<OrcamentoItem> itens = carregarItensOrcamento(selected.getId());
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

    private void carregarDados() {
        try {
            // Carregar clientes
            comboClientes.removeAllItems();
            comboFiltroClientes.removeAllItems();
            comboFiltroClientes.addItem("Todos");
            List<Cliente> clientes = service.listarClientes();
            for (Cliente c : clientes) {
                comboClientes.addItem(c);
                comboFiltroClientes.addItem(c.toString());
            }

            // Carregar itens
            DefaultListModel<Item> modelItens = new DefaultListModel<>();
            for (Item i : service.listarItens()) {
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
            List<Orcamento> todosOrcamentos = service.listarOrcamentos();
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
                service.cadastrarCliente(cliente);
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
                service.atualizarCliente(selected);
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
                service.deletarCliente(selected.getId());
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
                service.cadastrarItem(new Item(descricao.trim(), preco, dimensao));
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
                service.atualizarItem(selected);
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
                service.deletarItem(selected.getId());
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
                service.criarOrcamento(orc);
                JOptionPane.showMessageDialog(this, "Orçamento salvo!");
                modelItensOrcamento.clear();
                carregarDados();
                orcamentoEditando = null; // Resetar edição
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
                List<OrcamentoItem> itensOrcamento = carregarItensOrcamento(selected.getId());
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

                String mensagem = service.gerarMensagemOrcamento(orcamentoFiltrado);
                int resposta = JOptionPane.showConfirmDialog(this,
                        "Pré-visualização da mensagem:\n\n" + mensagem + "\n\nDeseja enviar esta mensagem?",
                        "Pré-visualização do Orçamento",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.INFORMATION_MESSAGE);

                if (resposta == JOptionPane.YES_OPTION) {
                    try {
                        service.enviarOrcamentoViaWhatsApp(orcamentoFiltrado);
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
                List<OrcamentoItem> itens = carregarItensOrcamento(selected.getId());
                for (OrcamentoItem item : itens) {
                    orcamentoEditando.adicionarItem(item.getItem(), item.getQuantidade());
                }

                JDialog editDialog = new JDialog(this, "Editar Orçamento", true);
                editDialog.setLayout(new BorderLayout(10, 10));
                editDialog.setSize(400, 300);

                JLabel labelInfo = new JLabel("Cliente: " + selected.getCliente().getNome() + " | Data: " + new java.text.SimpleDateFormat("dd-MM-yyyy").format(selected.getData()));
                editDialog.add(labelInfo, BorderLayout.NORTH);

                JList<OrcamentoItem> listEditItens = new JList<>(modelItensOrcamento);
                listEditItens.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
                JScrollPane scrollEdit = new JScrollPane(listEditItens);
                editDialog.add(scrollEdit, BorderLayout.CENTER);

                JPanel panelButtons = new JPanel(new FlowLayout());
                JButton btnRemover = new JButton("Remover Itens Selecionados");
                JButton btnAdicionarNovo = new JButton("Adicionar Novo Item");
                JButton btnSalvar = new JButton("Salvar Alterações");
                JButton btnCancelar = new JButton("Cancelar");

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
                        service.atualizarOrcamento(orcamentoEditando);
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
                    service.deletarOrcamento(selected.getId());
                    JOptionPane.showMessageDialog(this, "Orçamento deletado com sucesso!");
                    carregarDados(); // Recarrega a lista de orçamentos
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

    private List<OrcamentoItem> carregarItensOrcamento(int orcamentoId) throws SQLException {
        try (var conn = dao.DatabaseConnection.getConnection()) {
            return orcamentoDAO.carregarItensOrcamento(conn, orcamentoId);
        }
    }

    private double calcularTotalItens(List<OrcamentoItem> itens) {
        double total = 0.0;
        for (OrcamentoItem item : itens) {
            total += item.getItem().getPrecoUnitario() * item.getQuantidade();
        }
        return total;
    }
}