package pertemuan.pkg16;

import javax.swing.table.DefaultTableModel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import static pertemuan.pkg16.dbkoneksi.koneksi;

/**
 *
 * @author ZeroIchiro0
 * Hari/Tgl Selasa, 01 July 2025
 */

public class fSketchi extends javax.swing.JFrame {

    DefaultTableModel DM = new DefaultTableModel();

    public fSketchi() throws SQLException {
        initComponents();
        TM.setModel(DM);
        DM.addColumn("Code");
        DM.addColumn("Date");
        DM.addColumn("Page");
        DM.addColumn("Author Name");
        DM.addColumn("Job");
        DM.addColumn("Payment");
        
        txPAYMENT.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                double nilai = unformatRupiah(txPAYMENT.getText());
                txPAYMENT.setText(formatRupiah(nilai));
            }
        });
        
        cbJOB.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "" }));

        cleartextfield();
        this.ListDT();
        tombol(false);
        cADD.setEnabled(true);
        fieldIsian(false);
        txCODE.setEditable(false);
        updateTotalPayment();
    }
    
    private String generateAutoCode() throws SQLException {
    Connection cnn = koneksi();
    String autoCode = "1";

        if (!cnn.isClosed()) {
            PreparedStatement PS = cnn.prepareStatement("SELECT Code FROM sk ORDER BY Code ASC");
            ResultSet RS = PS.executeQuery();

            int i = 1;
            while (RS.next()) {
                String current = RS.getString("Code");
                if (!current.equals(String.valueOf(i))) {
                    break; // jika ditemukan lubang, isi di sana
                }
                i++;
            }
            autoCode = String.valueOf(i);
            cnn.close();
        }

        return autoCode;
    }
    
    private void reorderCode() throws SQLException {
    Connection cnn = koneksi();

        if (!cnn.isClosed()) {
            PreparedStatement select = cnn.prepareStatement("SELECT * FROM sk ORDER BY Code ASC");
            ResultSet rs = select.executeQuery();

            int newCode = 1;
            while (rs.next()) {
                String oldCode = rs.getString("Code");

                PreparedStatement update = cnn.prepareStatement("UPDATE sk SET Code=? WHERE Code=?");
                update.setString(1, String.valueOf(newCode));
                update.setString(2, oldCode);
                update.executeUpdate();

                newCode++;
            }

            cnn.close();
        }
    }

    
    private String formatRupiah(double amount) {
        DecimalFormat kursID = (DecimalFormat) NumberFormat.getCurrencyInstance(new Locale ("id", "ID"));
        DecimalFormatSymbols simbol = kursID.getDecimalFormatSymbols();
        simbol.setCurrencySymbol("Rp ");
        simbol.setGroupingSeparator('.');
        simbol.setMonetaryDecimalSeparator(',');   // kalau mau 100.000,00 ganti setMaximumFractionDigits(2)
        kursID.setDecimalFormatSymbols(simbol);
        kursID.setMaximumFractionDigits(0);
        return kursID.format(amount);
    }

    private double unformatRupiah(String text) {
    String numeric = text.replaceAll("[^\\d]", "");
    return numeric.isEmpty() ? 0 : Double.parseDouble(numeric);
    }

    private void updateTotalPayment() {
        double total = 0;
        DefaultTableModel model = (DefaultTableModel) TM.getModel();

        for (int i = 0; i < model.getRowCount(); i++) {
            Object value = model.getValueAt(i, 5);
            if (value != null) {
                double bayar = unformatRupiah(value.toString());
                total += bayar;
            }
        }

        tPAY.setText(formatRupiah(total));
    }

    
    private void storedta()throws SQLException{
        if (!txCODE.getText().equals("")){
            
            Connection cnn = koneksi();
            if(!cnn.isClosed()){
                PreparedStatement PS = cnn.prepareStatement("INSERT INTO sk(`Code`, `Date`, `Page`, `Author Name`, `Job`, `Payment`) VALUES(?,?,?,?,?,?);");
                PS.setString(1, txCODE.getText());
                
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String tanggalFormatted = sdf.format(txDATE.getDate());
                
                PS.setString(2, tanggalFormatted);
                PS.setString(3, txPAGE.getText());
                PS.setString(4, txNAMEs.getText());
                PS.setString(5, cbJOB.getSelectedItem().toString());
                PS.setDouble(6, unformatRupiah (txPAYMENT.getText()));
                PS.executeUpdate();
                cnn.close();
            }
            
        }         
        
    }
    
    private void updatedta()throws SQLException{
        Connection cnn = koneksi();
        if(!cnn.isClosed()){
            PreparedStatement PS = cnn.prepareStatement("UPDATE sk SET `Date`=?, `Page`=?, `Author Name`=?, `Job`=?, `Payment`=? WHERE `Code`=?;");
            
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String tanggalFormatted = sdf.format(txDATE.getDate());
            
            PS.setString(1, tanggalFormatted);
            PS.setString(2, txPAGE.getText());
            PS.setString(3, txNAMEs.getText());
            Object selectedJob = cbJOB.getSelectedItem();
            PS.setString(4, selectedJob != null ? selectedJob.toString() : "");
            PS.setDouble(5, unformatRupiah(txPAYMENT.getText()));
            PS.setString(6, txCODE.getText());
            PS.executeUpdate();
            cnn.close();
        }
    }

    private void destroydta(String code) throws SQLException{
        Connection cnn = koneksi();
        if(!cnn.isClosed()){
            PreparedStatement PS = cnn.prepareStatement("DELETE FROM sk WHERE Code =?;");
            PS.setString(1, code);
            PS.executeUpdate();
            cnn.close();
        }
    }
    
    private void tombol(boolean opsi){
        cADD.setEnabled(opsi);
        cEDIT.setEnabled(opsi);
        cDELETE.setEnabled(opsi);
    }
    
    private void fieldIsian (boolean opsi){
        txCODE.setEnabled(opsi);
        txDATE.setEnabled(opsi);
        txPAGE.setEnabled(opsi);
        txNAMEs.setEnabled(opsi);
        cbJOB.setEnabled(opsi);
        txPAYMENT.setEnabled(opsi);
    }
    
    private void cleartextfield(){
        txCODE.setText("");
        txDATE.setDate(new java.util.Date());
        txPAGE.setText("");
        txNAMEs.setText("");
        cbJOB.setSelectedIndex(-1);
        txPAYMENT.setText("");
    }
    
    private void ListDT() throws SQLException{
        Connection cnn = koneksi();
        
        DM.getDataVector().removeAllElements();
        DM.fireTableDataChanged();
        
        if(!cnn.isClosed()){
            PreparedStatement PS = cnn.prepareStatement("SELECT * FROM sk;");
            ResultSet RS = PS.executeQuery();
            
            while(RS.next()){
                Object[] data = new Object[6];
                data[0] = RS.getString("Code");
                data[1] = RS.getString("Date");
                data[2] = RS.getString("Page");
                data[3] = RS.getString("Author Name");
                data[4] = RS.getString("Job");
                double pay = RS.getDouble("Payment");
                data[5] = formatRupiah(pay);

                
                DM.addRow(data);
            }    
            cnn.close();
        }
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        TM = new javax.swing.JTable();
        jLabel2 = new javax.swing.JLabel();
        txCODE = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        txPAGE = new javax.swing.JTextField();
        txPAYMENT = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        txNAMEs = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        cADD = new javax.swing.JButton();
        cEDIT = new javax.swing.JButton();
        cDELETE = new javax.swing.JButton();
        cCLOSE = new javax.swing.JButton();
        txDATE = new com.toedter.calendar.JDateChooser();
        cbJOB = new javax.swing.JComboBox<>();
        jPanel1 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        tPAY = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        jLabel1.setFont(new java.awt.Font("Monospaced", 1, 25)); // NOI18N
        jLabel1.setText("PENCATATAN GAJI KANTOR SKETCHI STUDIO");

        TM.setFont(new java.awt.Font("Monospaced", 0, 15)); // NOI18N
        TM.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Code", "Date", "Page", "Author Name", "Job", "Payment"
            }
        ));
        TM.setShowGrid(false);
        TM.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                TMMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(TM);

        jLabel2.setFont(new java.awt.Font("Monospaced", 1, 15)); // NOI18N
        jLabel2.setText("Code");

        txCODE.setFont(new java.awt.Font("Monospaced", 0, 12)); // NOI18N
        txCODE.setText("jTextField1");

        jLabel3.setFont(new java.awt.Font("Monospaced", 1, 15)); // NOI18N
        jLabel3.setText("Date");

        jLabel4.setFont(new java.awt.Font("Monospaced", 1, 15)); // NOI18N
        jLabel4.setText("Page");

        txPAGE.setFont(new java.awt.Font("Monospaced", 0, 12)); // NOI18N
        txPAGE.setText("jTextField1");

        txPAYMENT.setFont(new java.awt.Font("Monospaced", 0, 12)); // NOI18N
        txPAYMENT.setText("jTextField1");

        jLabel5.setFont(new java.awt.Font("Monospaced", 1, 15)); // NOI18N
        jLabel5.setText("Payment");

        jLabel6.setFont(new java.awt.Font("Monospaced", 1, 15)); // NOI18N
        jLabel6.setText("Job");

        txNAMEs.setFont(new java.awt.Font("Monospaced", 0, 12)); // NOI18N
        txNAMEs.setText("jTextField1");

        jLabel7.setFont(new java.awt.Font("Monospaced", 1, 15)); // NOI18N
        jLabel7.setText("Author Name");

        cADD.setFont(new java.awt.Font("Monospaced", 0, 15)); // NOI18N
        cADD.setText("ADD");
        cADD.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cADDActionPerformed(evt);
            }
        });

        cEDIT.setFont(new java.awt.Font("Monospaced", 0, 15)); // NOI18N
        cEDIT.setText("EDIT");
        cEDIT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cEDITActionPerformed(evt);
            }
        });

        cDELETE.setFont(new java.awt.Font("Monospaced", 0, 15)); // NOI18N
        cDELETE.setText("DELETE");
        cDELETE.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cDELETEActionPerformed(evt);
            }
        });

        cCLOSE.setFont(new java.awt.Font("Monospaced", 0, 15)); // NOI18N
        cCLOSE.setText("CLOSE");
        cCLOSE.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cCLOSEActionPerformed(evt);
            }
        });

        txDATE.setDateFormatString("d MM y");

        cbJOB.setFont(new java.awt.Font("Monospaced", 0, 12)); // NOI18N
        cbJOB.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "     ", "Color", "Shadow", "Color + Shadow" }));
        cbJOB.setBorder(null);

        jLabel8.setFont(new java.awt.Font("Monospaced", 1, 16)); // NOI18N
        jLabel8.setText("Total Payment");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(265, 265, 265)
                .addComponent(jLabel8)
                .addContainerGap(240, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        tPAY.setFont(new java.awt.Font("Monospaced", 1, 15)); // NOI18N
        tPAY.setText("Rp 0");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tPAY, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(tPAY)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1)
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(122, 122, 122)
                        .addComponent(jLabel1)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(34, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(cADD, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(cEDIT, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(cDELETE, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(cCLOSE, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3)
                            .addComponent(jLabel4)
                            .addComponent(txCODE)
                            .addComponent(txPAGE)
                            .addComponent(txDATE, javax.swing.GroupLayout.PREFERRED_SIZE, 350, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5)
                            .addComponent(jLabel6)
                            .addComponent(jLabel7)
                            .addComponent(txPAYMENT, javax.swing.GroupLayout.PREFERRED_SIZE, 350, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txNAMEs, javax.swing.GroupLayout.PREFERRED_SIZE, 350, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cbJOB, javax.swing.GroupLayout.PREFERRED_SIZE, 350, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(28, 28, 28))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 257, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txCODE, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel3))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txNAMEs, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel6)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(txDATE, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txPAGE, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(cbJOB, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txPAYMENT, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(41, 41, 41)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cADD, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cEDIT, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cDELETE, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cCLOSE, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(93, 93, 93))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void TMMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_TMMouseClicked
        txCODE.setText(TM.getValueAt(TM.getSelectedRow(), 0).toString());
        
        try {
        java.util.Date date = new java.text.SimpleDateFormat("yyyy-MM-dd")
                               .parse(TM.getValueAt(TM.getSelectedRow(), 1).toString());
        txDATE.setDate(date);                       // ← ganti
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        
        txPAGE.setText(TM.getValueAt(TM.getSelectedRow(), 2).toString());
        txNAMEs.setText(TM.getValueAt(TM.getSelectedRow(), 3).toString());
        cbJOB.setSelectedItem(TM.getValueAt(TM.getSelectedRow(), 4).toString());
        txPAYMENT.setText(TM.getValueAt(TM.getSelectedRow(), 5).toString());
        cEDIT.setEnabled(true);
        cDELETE.setEnabled(true);
    }//GEN-LAST:event_TMMouseClicked

    private void cADDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cADDActionPerformed
        if(cADD.getText().equals("ADD")){
            cADD.setText("SAVE");
            cCLOSE.setText("CANCEL");
            cEDIT.setEnabled(false);
            cDELETE.setEnabled(false);
            cleartextfield();
            cbJOB.setModel(new javax.swing.DefaultComboBoxModel<>(
                new String[] { "Color", "Shadow", "Color + Shadow" }
            ));
            
            cbJOB.setSelectedIndex(-1);

            fieldIsian(true);
            txDATE.setDate(new java.util.Date()); // set tanggal hari ini otomatis

            try {
            txCODE.setText(generateAutoCode());
            } catch (SQLException ex) {
                Logger.getLogger(fSketchi.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }else{
            cADD.setText("ADD");
            cCLOSE.setText("CLOSE");
        
            try {
                storedta();
                ListDT();
            } catch (SQLException ex) {
                Logger.getLogger(fSketchi.class.getName()).log(Level.SEVERE, null, ex);
            }
            cleartextfield();
            
            updateTotalPayment();

            fieldIsian(false);
        }
    }//GEN-LAST:event_cADDActionPerformed

    private void cEDITActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cEDITActionPerformed
        if(cEDIT.getText().equals("EDIT")){
            cEDIT.setText("SAVE");
            cCLOSE.setText("CANCEL");
            cADD.setEnabled(false);
            cDELETE.setEnabled(false);
            fieldIsian(true);
            txCODE.setEditable(false);
            
            cbJOB.setModel(new javax.swing.DefaultComboBoxModel<>(
                new String[] { "Color", "Shadow", "Color + Shadow" }
            ));

            
        }else{
            cEDIT.setText("EDIT");
            cCLOSE.setText("CLOSE");
        
            try {
                updatedta();
                ListDT();
            } catch (SQLException ex) {
                Logger.getLogger(fSketchi.class.getName()).log(Level.SEVERE, null, ex);
            }
            cleartextfield();
            updateTotalPayment();
            fieldIsian(false);
            cADD.setEnabled(true);
            cEDIT.setEnabled(false);
        }
    }//GEN-LAST:event_cEDITActionPerformed

    private void cDELETEActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cDELETEActionPerformed
        if(cDELETE.getText().equals("DELETE")){
            String code = txCODE.getText();
            
            int jwb = JOptionPane.showOptionDialog(this,
                    "Are you sure you want to delete the data? CODE"+ code, 
                    "Confirm Delete Data", 
                    JOptionPane.YES_NO_OPTION, 
                    JOptionPane.ERROR_MESSAGE, 
                    null, null, null);
        
            if(jwb == JOptionPane.YES_OPTION){
                try {
                    destroydta(code);
                    reorderCode();
                    ListDT();
                } catch (SQLException ex) {
                    Logger.getLogger(fSketchi.class.getName()).log(Level.SEVERE, null, ex);
                }
                    cleartextfield();
                    updateTotalPayment();
                    fieldIsian(false);
                    cADD.setEnabled(true);
                    cEDIT.setEnabled(false);
                    cDELETE.setEnabled(false);
            }
        }
    }//GEN-LAST:event_cDELETEActionPerformed

    private void cCLOSEActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cCLOSEActionPerformed
        if(cCLOSE.getText().equals("CLOSE")){
            int jwb = JOptionPane.showOptionDialog(this,
                        "Are you sure you want to close the application?", 
                        "Confirm Close Application", 
                        JOptionPane.YES_NO_OPTION, 
                        JOptionPane.ERROR_MESSAGE, 
                        null, null, null);
            if (jwb == JOptionPane.YES_OPTION){
                System.exit(0);
            }
        } else{
            cCLOSE.setText("CLOSE");
            cADD.setText("ADD");
            cEDIT.setText("EDIT");
            cADD.setEnabled(true);
            cDELETE.setEnabled(false);
            cEDIT.setEnabled(false);
            cleartextfield();
            fieldIsian(false);
        }
    }//GEN-LAST:event_cCLOSEActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(fSketchi.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(fSketchi.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(fSketchi.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(fSketchi.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    new fSketchi().setVisible(true);
                } catch (SQLException ex) {
                    Logger.getLogger(fSketchi.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable TM;
    private javax.swing.JButton cADD;
    private javax.swing.JButton cCLOSE;
    private javax.swing.JButton cDELETE;
    private javax.swing.JButton cEDIT;
    private javax.swing.JComboBox<String> cbJOB;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel tPAY;
    private javax.swing.JTextField txCODE;
    private com.toedter.calendar.JDateChooser txDATE;
    private javax.swing.JTextField txNAMEs;
    private javax.swing.JTextField txPAGE;
    private javax.swing.JTextField txPAYMENT;
    // End of variables declaration//GEN-END:variables
}
