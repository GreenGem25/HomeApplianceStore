package ohio.rizz.homeappliancestore.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "settings")
@Getter
@Setter
public class Settings {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "shop_name", nullable = false)
    private String shopName = "Магазин техники";

    @Column(name = "address")
    private String address = "г. Ваш Город, ул. Примерная, 1";

    @Column(name = "phone")
    private String phone = "+7 (000) 000-00-00";

    @Column(name = "email")
    private String email = "info@shop.ru";

}