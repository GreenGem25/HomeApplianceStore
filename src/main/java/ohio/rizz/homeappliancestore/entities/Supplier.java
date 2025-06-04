package ohio.rizz.homeappliancestore.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "suppliers")
@Getter
@Setter
public class Supplier {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "supplier_id")
    private Long id;

    @Column(name = "name", nullable = false)
    private String name = "No name";
    @Column(name = "contact_name")
    private String contactName = "No contact name";
    @Column(name = "phone")
    private String phone = "No phone";
    @Column(name = "email")
    private String email = "No email";
    @Column(name = "address")
    private String address = "No address";

    @OneToMany(mappedBy = "supplier")
    private List<Product> products = new ArrayList<>();

}
