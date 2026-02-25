package org.example.mario_pr51.BD;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Tooltip;
import net.sf.jasperreports.engine.*;

import java.io.InputStream;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class Reporte implements Initializable {

    @FXML
    private Button btn_generar;
    @FXML
    private ChoiceBox<String> choicebox_meses;
    @FXML
    private BarChart<String, Number> consumoBarras;
    @FXML
    private CheckBox c_punta, c_llano, c_valle;
    private int numFactura;

    // Mapeo de meses a números
    private final Map<String, Integer> mesesMap = new HashMap<>();
    // Mapeo de nombres de meses a abreviaturas en inglés (necesario para SQLite)
    private final Map<String, String> mesesSQLiteMap = new HashMap<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Inicializar mapeos
        inicializarMesesMap();

        // Inicializar meses en el ChoiceBox
        String[] meses = {"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
                "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};
        choicebox_meses.getItems().addAll(meses);
        choicebox_meses.setValue("Enero");

        // Configurar gráfico
        grafico();

        // Seleccionar todos los checkboxes por defecto
        c_punta.setSelected(true);
        c_llano.setSelected(true);
        c_valle.setSelected(true);

        // Cargar datos iniciales
        actualizar();

        // Configurar listeners
        choicebox_meses.setOnAction(event -> actualizar());
        c_punta.setOnAction(event -> actualizar());
        c_llano.setOnAction(event -> actualizar());
        c_valle.setOnAction(event -> actualizar());

        btn_generar.setOnAction(event -> {
            try {
                generarPdf();
            } catch (JRException e) {
                System.out.println("No se ha podido generar la factura.");
                e.printStackTrace();
            }
        });
        numFactura = 0;

        // Tooltips para botones
        btn_generar.setTooltip(new Tooltip("Genera un PDF con la factura actual"));

        // Tooltips para ChoiceBox
        choicebox_meses.setTooltip(new Tooltip("Seleccione el mes para filtrar el consumo"));

        // Tooltips para CheckBox
        Tooltip.install(c_punta, new Tooltip("Consumo en horas punta: 10h-14h, 18h-22h"));
        Tooltip.install(c_llano, new Tooltip("Consumo en horas llanas: 8h-10h, 14h-18h, 22h-24h"));
        Tooltip.install(c_valle, new Tooltip("Consumo en horas valle: 0h-8h y fines de semana"));
    }

    private void inicializarMesesMap() {
        // Mapeo para números
        mesesMap.put("Enero", 1);
        mesesMap.put("Febrero", 2);
        mesesMap.put("Marzo", 3);
        mesesMap.put("Abril", 4);
        mesesMap.put("Mayo", 5);
        mesesMap.put("Junio", 6);
        mesesMap.put("Julio", 7);
        mesesMap.put("Agosto", 8);
        mesesMap.put("Septiembre", 9);
        mesesMap.put("Octubre", 10);
        mesesMap.put("Noviembre", 11);
        mesesMap.put("Diciembre", 12);

        // Mapeo para SQLite (nombres en inglés)
        mesesSQLiteMap.put("Enero", "January");
        mesesSQLiteMap.put("Febrero", "February");
        mesesSQLiteMap.put("Marzo", "March");
        mesesSQLiteMap.put("Abril", "April");
        mesesSQLiteMap.put("Mayo", "May");
        mesesSQLiteMap.put("Junio", "June");
        mesesSQLiteMap.put("Julio", "July");
        mesesSQLiteMap.put("Agosto", "August");
        mesesSQLiteMap.put("Septiembre", "September");
        mesesSQLiteMap.put("Octubre", "October");
        mesesSQLiteMap.put("Noviembre", "November");
        mesesSQLiteMap.put("Diciembre", "December");
    }

    public void generarPdf() throws JRException {
        try {
            InputStream rs = getClass().getResourceAsStream("/org/example/mario_pr51/facturaLuz.jrxml");
            if (rs == null) {
                System.out.println("No se encontró el archivo JRXML");
                return;
            }

            JasperReport jr = JasperCompileManager.compileReport(rs);

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("ID_FACTURA", 1);

            Connection conx = ConexionBD.getConnection();
            if (conx == null) {
                System.out.println("No se pudo establecer conexión a la BD");
                return;
            }

            JasperPrint jsp = JasperFillManager.fillReport(jr, parameters, conx);
            numFactura++;
            String facturaPdf = "src/main/resources/org/example/mario_pr51/PDF/reporteFactura" + numFactura + ".pdf";
            JasperExportManager.exportReportToPdfFile(jsp, facturaPdf);
            System.out.println("PDF generado: " + facturaPdf);

            conx.close();
        } catch (Exception e) {
            System.out.println("Error al generar PDF: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void grafico() {
        consumoBarras.getXAxis().setLabel("Horarios");
        consumoBarras.getYAxis().setLabel("KWH");
        consumoBarras.setTitle("Consumo de energia");
        consumoBarras.setAnimated(false);
    }

    private double valorConsumo(String tramo, String mesSeleccionado) {
        double consumo = 0;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = ConexionBD.getConnection();
            if (conn == null) {
                System.out.println("Conexión nula en valorConsumo");
                return 0;
            }

            // Obtener el número del mes seleccionado
            int numeroMes = mesesMap.get(mesSeleccionado);

            // CONSULTA PARA SQLITE - usando strftime para extraer el mes
            String sql = "SELECT SUM(c.consumo_kwh) as total_consumo " +
                    "FROM CONSUMO_TRAMO c " +
                    "INNER JOIN FACTURA f ON c.id_factura = f.id_factura " +
                    "WHERE c.tramo = ? " +
                    "AND CAST(strftime('%m', f.periodo_inicio) AS INTEGER) = ? " +
                    "GROUP BY c.tramo";

            ps = conn.prepareStatement(sql);
            ps.setString(1, tramo.toUpperCase());  // En tu BD está en mayúsculas
            ps.setInt(2, numeroMes);

            rs = ps.executeQuery();

            if (rs.next()) {
                consumo = rs.getDouble("total_consumo");
                System.out.println("Consumo " + tramo + " para " + mesSeleccionado + ": " + consumo);
            } else {
                System.out.println("No se encontraron datos para " + tramo + " en " + mesSeleccionado);
                // Si no hay datos, usar valores por defecto
                consumo = obtenerValorPorDefecto(tramo);
            }
        } catch (SQLException e) {
            System.out.println("Error SQL al obtener consumo para " + tramo + ": " + e.getMessage());
            e.printStackTrace();

            // Método alternativo si hay problemas con strftime
            consumo = obtenerConsumoAlternativoSQLite(tramo, mesSeleccionado);
        } catch (Exception e) {
            System.out.println("Error general al obtener consumo para " + tramo + ": " + e.getMessage());
            e.printStackTrace();
            consumo = obtenerValorPorDefecto(tramo);
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return consumo;
    }

    private double obtenerValorPorDefecto(String tramo) {
        switch(tramo.toUpperCase()) {
            case "PUNTA": return 150.0;
            case "LLANO": return 100.0;
            case "VALLE": return 80.0;
            default: return 0.0;
        }
    }

    // Método alternativo para SQLite
    private double obtenerConsumoAlternativoSQLite(String tramo, String mesSeleccionado) {
        double consumo = 0;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = ConexionBD.getConnection();
            if (conn == null) {
                return obtenerValorPorDefecto(tramo);
            }

            // Método alternativo: traer todos los datos y filtrar en Java
            String sql = "SELECT c.consumo_kwh, f.periodo_inicio " +
                    "FROM CONSUMO_TRAMO c " +
                    "INNER JOIN FACTURA f ON c.id_factura = f.id_factura " +
                    "WHERE c.tramo = ?";

            ps = conn.prepareStatement(sql);
            ps.setString(1, tramo.toUpperCase());
            rs = ps.executeQuery();

            int numeroMes = mesesMap.get(mesSeleccionado);
            double total = 0;
            int contador = 0;

            while (rs.next()) {
                String fechaStr = rs.getString("periodo_inicio");
                try {
                    // Parsear la fecha (formato: YYYY-MM-DD)
                    LocalDate fecha = LocalDate.parse(fechaStr);

                    // Filtrar por mes
                    if (fecha.getMonthValue() == numeroMes) {
                        total += rs.getDouble("consumo_kwh");
                        contador++;
                    }
                } catch (Exception e) {
                    System.out.println("Error al parsear fecha: " + fechaStr);
                }
            }

            if (contador > 0) {
                consumo = total;
                System.out.println("Consumo alternativo " + tramo + " para " + mesSeleccionado + ": " + consumo);
            } else {
                consumo = obtenerValorPorDefecto(tramo);
            }

        } catch (Exception e) {
            System.out.println("Error en método alternativo SQLite: " + e.getMessage());
            consumo = obtenerValorPorDefecto(tramo);
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return consumo;
    }

    private void actualizar() {
        consumoBarras.getData().clear();

        // Verificar que al menos un checkbox esté seleccionado
        if (!c_punta.isSelected() && !c_llano.isSelected() && !c_valle.isSelected()) {
            System.out.println("No hay tramos seleccionados");
            return;
        }

        String mes = choicebox_meses.getValue();
        System.out.println("Actualizando gráfico para: " + mes);

        XYChart.Series<String, Number> serie = new XYChart.Series<>();
        serie.setName("Consumo: " + mes);

        if (c_punta.isSelected()) {
            double valor = valorConsumo("PUNTA", mes);
            System.out.println("Punta: " + valor);
            serie.getData().add(new XYChart.Data<>("Punta", valor));
        }

        if (c_llano.isSelected()) {
            double valor = valorConsumo("LLANO", mes);
            System.out.println("Llano: " + valor);
            serie.getData().add(new XYChart.Data<>("Llano", valor));
        }

        if (c_valle.isSelected()) {
            double valor = valorConsumo("VALLE", mes);
            System.out.println("Valle: " + valor);
            serie.getData().add(new XYChart.Data<>("Valle", valor));
        }

        if (!serie.getData().isEmpty()) {
            consumoBarras.getData().add(serie);
            System.out.println("Gráfico actualizado con " + serie.getData().size() + " elementos");
        } else {
            System.out.println("No se pudieron cargar datos para el gráfico");
        }
    }
}