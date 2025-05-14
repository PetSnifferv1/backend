package br.com.petsniffer.app.domain.pets;

public enum PetStatus {

    FOUND("achado"),
    LOST("perdido");

    private String status;

    PetStatus(String status){
        this.status = status;
    }

    public String getStatus(){
        return status;
    }

}
