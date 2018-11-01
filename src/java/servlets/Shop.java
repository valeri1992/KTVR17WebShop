/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets;

import entity.Customer;
import entity.Product;
import entity.Purchase;
import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import session.CustomerFacade;
import session.ProductFacade;
import session.PurchaseFacade;
import util.PageReturner;

/**
 *
 * @author agloi
 */
@WebServlet(name = "Shop", urlPatterns = {
    "/newProduct",
    "/addProduct",
    "/newCustomer",
    "/addCustomer",
    "/showProducts",
    "/showCustomer",
    "/shop",
    "/takeProduct",
    "/showTakeProduct",
    "/returnProduct",
    "/deleteProduct",
    })

public class Shop extends HttpServlet {
@EJB ProductFacade productFacade; 
@EJB CustomerFacade customerFacade;
@EJB PurchaseFacade purchaseFacade; 
    

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        request.setCharacterEncoding("UTF8");
        String path = request.getServletPath();
        if(null != path)switch (path) {
        case "/newProduct":
            request.getRequestDispatcher(PageReturner.getPage("newProduct")).forward(request, response);
            break;
        case "/addProduct":{
            String nameProduct= request.getParameter("name");
            String price = request.getParameter("price");
            String count=request.getParameter("count");
            Product product = new Product(nameProduct, new Integer(price),new Integer(count));
            productFacade.create(product);
            request.setAttribute("product", product);
            request.getRequestDispatcher(PageReturner.getPage("welcom")).forward(request, response);
                break;
            }
        case "/newCustomer":
            request.getRequestDispatcher(PageReturner.getPage("newCustomer")).forward(request, response);
            break;
        case "/addCustomer":{
            String name = request.getParameter("name");
            String surname = request.getParameter("surname");
            String money = request.getParameter("money");
            Customer customer = new Customer(name, surname, money);
            customerFacade.create(customer);
            request.setAttribute("customer", customer);
            request.getRequestDispatcher(PageReturner.getPage("welcom")).forward(request, response);
                break;
            }
        case "/showProducts":{
            List<Product> listProducts = productFacade.findActived(true);
            request.setAttribute("listProducts", listProducts);
            request.getRequestDispatcher(PageReturner.getPage("listProducts")).forward(request, response);
                break;
            }
        case "/showCustomer":
            List<Customer> listCustomer = customerFacade.findAll();
            request.setAttribute("listCustomer", listCustomer());
            request.getRequestDispatcher(PageReturner.getPage("listCustomer")).forward(request, response);
            break;
        case "/purchase":
            request.setAttribute("listProducts", productFacade.findActived(true));
            request.setAttribute("listCustomer", customerFacade.findAll());
            request.getRequestDispatcher(PageReturner.getPage("takeProduct")).forward(request, response);
            break;
        case "/showTakeProduct":{
            List<Purchase> takeProducts = purchaseFacade.findTakeProducts();
            request.setAttribute("takeProducts", takeProducts);
            request.getRequestDispatcher(PageReturner.getPage("listTakeproduct")).forward(request, response);
                break;
            }
        case "/takeProduct":{
            String selectedProduct = request.getParameter("selectedProduct");
            String selectedCustomer = request.getParameter("selectedCustomer");
            Product product = productFacade.find(new Long(selectedProduct));
            Customer customer = customerFacade.find(new Long(selectedCustomer));
            Calendar c = new GregorianCalendar();
            Purchase purchase;
            if(product.getCount()>0){
                product.setCount(product.getCount()-1);
                productFacade.edit(product);
                purchase = new Purchase(product, customer, c.getTime(), null); 
                purchaseFacade.create(purchase);
            }else{
                request.setAttribute("info", "данного продукта нет на складе");
            }
            List<Purchase> takeProducts = purchaseFacade.findTakeProducts();
            request.setAttribute("takeProducts", takeProducts);
            request.getRequestDispatcher(PageReturner.getPage("listTakeproduct")).forward(request, response);
                break;
            }
//        case "/returnProduct":{
//            String returnProductId = request.getParameter("returnproductId");
//            Purchase purchase = purchaseFacade.find(new Long(returnProductId));
//            Calendar c = new GregorianCalendar();
//            purchase.setDateReturn(c.getTime());
//            purchaseFacade.edit(purchase);
//            List<Purchase> takePurchases = purchaseFacade.findTakeProducts();
//            Object takeProducts = null;
//            request.setAttribute("takeProducts", takeProducts);
//            request.getRequestDispatcher(PageReturner.getPage("listTakeProduct")).forward(request, response);
//                break;
//            }
        case "/deleteProduct":{
            String deleteProductId = request.getParameter("deleteProductId");
            Product product = productFacade.find(new Long(deleteProductId));
            product.setActive(Boolean.FALSE);
            productFacade.edit(product);
            List<Product> listProducts = productFacade.findActived(true);
            request.setAttribute("listProducts", listProducts);
            request.getRequestDispatcher(PageReturner.getPage("listTakeProduct")).forward(request, response);
                break;
            }
        default:
            request.getRequestDispatcher(PageReturner.getPage("welcom")).forward(request, response);
            break;
    }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    public void init() throws ServletException {
        getServletContext().setAttribute("customers", customerFacade.findAll()); 
    }


    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

    private Object listCustomer() {
    return null;
      
    }

}
