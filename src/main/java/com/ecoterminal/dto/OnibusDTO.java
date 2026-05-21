package com.ecoterminal.dto;

import com.ecoterminal.enums.PadraoMotor;
import com.ecoterminal.enums.TipoCombustivel;
import com.ecoterminal.enums.TipoOnibus;
import com.ecoterminal.model.Onibus;
public class OnibusDTO {

    private Long id;
    private String prefixo;
    private TipoOnibus tipo;
    private PadraoMotor padraoMotor;
    private TipoCombustivel combustivel;
    private boolean temArCondicionado;
    private double kmAnuais;
    private int anoFabricacao;
    private Long terminalId;
    private String terminalNome;

    public static OnibusDTO fromEntity(Onibus o) {
        OnibusDTO dto = new OnibusDTO();
        dto.id = o.getId();
        dto.prefixo = o.getPrefixo();
        dto.tipo = o.getTipo();
        dto.padraoMotor = o.getPadraoMotor();
        dto.combustivel= o.getCombustivel();
        dto.temArCondicionado = o.isTemArCondicionado();
        dto.kmAnuais= o.getKmAnuais();
        dto.anoFabricacao = o.getAnoFabricacao();
        if (o.getTerminal() != null) {
            dto.terminalId= o.getTerminal().getId();
            dto.terminalNome = o.getTerminal().getNome();
        }
        return dto;
    }
    public Long getId(){
         return id; }
    public void setId(Long id){ 
        this.id = id; }

    public String getPrefixo(){
         return prefixo; }
    public void setPrefixo(String prefixo){
         this.prefixo = prefixo; }

    public TipoOnibus getTipo(){
         return tipo; }
    public void setTipo(TipoOnibus tipo){
         this.tipo = tipo; }

    public PadraoMotor getPadraoMotor(){ 
        return padraoMotor; }
    public void setPadraoMotor(PadraoMotor padraoMotor){
         this.padraoMotor = padraoMotor; }

    public TipoCombustivel getCombustivel(){
         return combustivel; }
    public void setCombustivel(TipoCombustivel combustivel){
         this.combustivel = combustivel; }

    public boolean isTemArCondicionado(){
         return temArCondicionado; }
    public void setTemArCondicionado(boolean temArCondicionado){
         this.temArCondicionado = temArCondicionado; }

    public double getKmAnuais(){ 
        return kmAnuais; }
    public void setKmAnuais(double kmAnuais){
         this.kmAnuais = kmAnuais; }

    public int getAnoFabricacao(){
         return anoFabricacao; }
    public void setAnoFabricacao(int anoFabricacao){ 
        this.anoFabricacao = anoFabricacao; }

    public Long getTerminalId(){
        return terminalId; }
    public void setTerminalId(Long terminalId){
         this.terminalId = terminalId; }

    public String getTerminalNome(){
         return terminalNome; }
    public void setTerminalNome(String terminalNome){
         this.terminalNome = terminalNome; }
}
 