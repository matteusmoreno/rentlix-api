package com.mtsolutions.domain.service;

import com.lowagie.text.Document;
import com.lowagie.text.PageSize;
import com.lowagie.text.html.simpleparser.HTMLWorker;
import com.lowagie.text.pdf.PdfWriter;
import com.mtsolutions.application.common.Features;
import com.mtsolutions.application.exception.ContractTemplateInvalidException;
import com.mtsolutions.application.exception.RentlixApiException;
import com.mtsolutions.domain.constant.AssetType;
import com.mtsolutions.domain.constant.MaritalStatus;
import com.mtsolutions.domain.constant.PricePeriod;
import com.mtsolutions.domain.entity.Asset;
import com.mtsolutions.domain.entity.Rental;
import com.mtsolutions.domain.entity.User;
import com.mtsolutions.domain.model.Address;
import com.mtsolutions.domain.model.PropertyDetails;
import com.mtsolutions.domain.model.VehicleDetails;
import jakarta.enterprise.context.ApplicationScoped;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;

@ApplicationScoped
public class ContractPdf {

    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public byte[] render(String template, Rental rental) throws RentlixApiException {
        String html = "<html><body>" + this.fill(template, rental) + "</body></html>";
        try {
            return this.pdf(html);
        } catch (Exception exception) {
            throw new ContractTemplateInvalidException(Features.GENERATE_RENTAL_CONTRACT);
        }
    }

    private String fill(String template, Rental rental) {
        Map<String, String> values = this.values(rental);
        String filled = template;
        for (Map.Entry<String, String> value : values.entrySet()) {
            filled = filled.replace("{{" + value.getKey() + "}}", value.getValue());
        }
        return filled;
    }

    private Map<String, String> values(Rental rental) {
        Asset asset = rental.getAsset();
        User lessee = rental.getLessee();
        Address lesseeAddress = lessee.getAddress();
        VehicleDetails vehicle = asset.getVehicle();
        PropertyDetails property = asset.getProperty();
        Address propertyAddress = property == null ? null : property.getAddress();
        Map<String, String> values = new LinkedHashMap<>();

        values.put("company.name", this.text(asset.getCompany().getName()));
        values.put("company.cnpj", this.text(asset.getCompany().getCnpj()));

        values.put("lessee.name", this.text(lessee.getName()));
        values.put("lessee.cpf", this.text(lessee.getCpf()));
        values.put("lessee.rg", this.text(lessee.getRg()));
        values.put("lessee.email", this.text(lessee.getEmail()));
        values.put("lessee.phone", this.text(lessee.getPhone()));
        values.put("lessee.birthDate", this.date(lessee.getBirthDate()));
        values.put("lessee.occupation", this.text(lessee.getOccupation()));
        values.put("lessee.maritalStatus", this.maritalStatus(lessee.getMaritalStatus()));
        values.put("lessee.zipCode", this.address(lesseeAddress, Address::getZipCode));
        values.put("lessee.street", this.address(lesseeAddress, Address::getStreet));
        values.put("lessee.number", this.address(lesseeAddress, Address::getNumber));
        values.put("lessee.complement", this.address(lesseeAddress, Address::getComplement));
        values.put("lessee.neighborhood", this.address(lesseeAddress, Address::getNeighborhood));
        values.put("lessee.city", this.address(lesseeAddress, Address::getCity));
        values.put("lessee.state", this.address(lesseeAddress, Address::getState));

        values.put("asset.name", this.text(asset.getName()));
        values.put("asset.description", this.text(asset.getDescription()));
        values.put("asset.type", this.assetType(asset.getType()));
        values.put("asset.price", this.money(asset.getPrice()));
        values.put("asset.pricePeriod", this.pricePeriod(asset.getPricePeriod()));

        values.put("vehicle.brand", vehicle == null ? "" : this.text(vehicle.getBrand()));
        values.put("vehicle.model", vehicle == null ? "" : this.text(vehicle.getModel()));
        values.put("vehicle.manufactureYear", vehicle == null ? "" : this.text(vehicle.getManufactureYear()));
        values.put("vehicle.modelYear", vehicle == null ? "" : this.text(vehicle.getModelYear()));
        values.put("vehicle.color", vehicle == null ? "" : this.text(vehicle.getColor()));
        values.put("vehicle.plate", vehicle == null ? "" : this.text(vehicle.getPlate()));
        values.put("vehicle.renavam", vehicle == null ? "" : this.text(vehicle.getRenavam()));
        values.put("vehicle.chassis", vehicle == null ? "" : this.text(vehicle.getChassis()));
        values.put("vehicle.engineCc", vehicle == null ? "" : this.text(vehicle.getEngineCc()));
        values.put("vehicle.mileage", vehicle == null ? "" : this.text(vehicle.getMileage()));

        values.put("property.zipCode", this.address(propertyAddress, Address::getZipCode));
        values.put("property.street", this.address(propertyAddress, Address::getStreet));
        values.put("property.number", this.address(propertyAddress, Address::getNumber));
        values.put("property.complement", this.address(propertyAddress, Address::getComplement));
        values.put("property.neighborhood", this.address(propertyAddress, Address::getNeighborhood));
        values.put("property.city", this.address(propertyAddress, Address::getCity));
        values.put("property.state", this.address(propertyAddress, Address::getState));
        values.put("property.totalArea", property == null ? "" : this.text(property.getTotalArea()));
        values.put("property.bedrooms", property == null ? "" : this.text(property.getBedrooms()));
        values.put("property.suites", property == null ? "" : this.text(property.getSuites()));
        values.put("property.bathrooms", property == null ? "" : this.text(property.getBathrooms()));
        values.put("property.parkingSpaces", property == null ? "" : this.text(property.getParkingSpaces()));
        values.put("property.furnished", property == null ? "" : this.yesNo(property.getFurnished()));
        values.put("property.petFriendly", property == null ? "" : this.yesNo(property.getPetFriendly()));
        values.put("property.hasElevator", property == null ? "" : this.yesNo(property.getHasElevator()));
        values.put("property.hasPool", property == null ? "" : this.yesNo(property.getHasPool()));
        values.put("property.hasGym", property == null ? "" : this.yesNo(property.getHasGym()));
        values.put("property.hasBarbecue", property == null ? "" : this.yesNo(property.getHasBarbecue()));
        values.put("property.condoFee", property == null ? "" : this.money(property.getCondoFee()));
        values.put("property.iptu", property == null ? "" : this.money(property.getIptu()));
        values.put("property.registrationNumber", property == null ? "" : this.text(property.getRegistrationNumber()));

        values.put("rental.startDate", this.date(rental.getStartDate()));
        values.put("rental.endDate", this.date(rental.getEndDate()));
        values.put("rental.price", this.money(rental.getPrice()));
        values.put("rental.pricePeriod", this.pricePeriod(rental.getPricePeriod()));
        return values;
    }

    private byte[] pdf(String html) throws Exception {
        Document document = new Document(PageSize.A4, 50, 50, 50, 50);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, output);
        document.open();
        HTMLWorker worker = new HTMLWorker(document);
        worker.parse(new StringReader(html));
        document.close();
        return output.toByteArray();
    }

    private String address(Address address, Function<Address, String> value) {
        if (address == null) {
            return "";
        }
        return this.text(value.apply(address));
    }

    private String text(Object value) {
        if (value == null) {
            return "";
        }
        return value.toString()
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }

    private String date(LocalDate value) {
        if (value == null) {
            return "";
        }
        return DATE.format(value);
    }

    private String money(BigDecimal value) {
        if (value == null) {
            return "";
        }
        return NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR")).format(value);
    }

    private String yesNo(Boolean value) {
        if (value == null) {
            return "";
        }
        return value ? "Sim" : "Não";
    }

    private String assetType(AssetType type) {
        if (type == null) {
            return "";
        }
        return switch (type) {
            case MOTORCYCLE -> "Moto";
            case CAR -> "Carro";
            case HOUSE -> "Casa";
            case APARTMENT -> "Apartamento";
        };
    }

    private String pricePeriod(PricePeriod period) {
        if (period == null) {
            return "";
        }
        return switch (period) {
            case DAY -> "dia";
            case WEEK -> "semana";
            case MONTH -> "mês";
            case SEMESTER -> "semestre";
            case YEAR -> "ano";
        };
    }

    private String maritalStatus(MaritalStatus status) {
        if (status == null) {
            return "";
        }
        return switch (status) {
            case SINGLE -> "Solteiro";
            case MARRIED -> "Casado";
            case DIVORCED -> "Divorciado";
            case WIDOWED -> "Viúvo";
            case UNKNOWN -> "";
        };
    }
}
