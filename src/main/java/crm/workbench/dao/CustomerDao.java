package crm.workbench.dao;

import crm.workbench.domain.Customer;

import java.util.List;

public interface CustomerDao {

    Customer getCustomerByName(String company);

    int save(Customer c1);

    List<String> getCustomerName(String name);
}
