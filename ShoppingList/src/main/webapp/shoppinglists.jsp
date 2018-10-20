<%@page contentType="text/html" pageEncoding="UTF-8" errorPage="ooops.jsp"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
        <title>Shopping lists shared with ${user.firstName} ${user.lastName}</title>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">

        <!-- Disabled cache (fix bug with user's avatars) -->
        <meta http-equiv="Cache-Control" content="no-cache, no-store, must-revalidate"/>
        <meta http-equiv="Pragma" content="no-cache"/>
        <meta http-equiv="Expires" content="0"/>
        <!-- Latest compiled and minified CSS -->
        <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/css/bootstrap.min.css" crossorigin="anonymous">
        <link rel="stylesheet" href="https://use.fontawesome.com/releases/v5.0.8/css/all.css" crossorigin="anonymous">
        <link rel="stylesheet" href="https://cdn.datatables.net/1.10.16/css/jquery.dataTables.min.css" crossorigin="anonymous">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/select2/4.0.6-rc.0/css/select2.min.css" crossorigin="anonymous">
        <link rel="stylesheet" href="../css/select2-bootstrap4.min">
        <link rel="stylesheet" href="../css/floating-labels.css">
        <link rel="stylesheet" href="../css/forms.css">
    </head>
    <body>

        <!-- NAVBAR -->

        <nav class="navbar fixed-top navbar-expand-lg navbar-dark bg-dark "> 
            <div class="container">
                <c:if test="${not empty user.avatarPath}">
                    <c:choose>
                        <c:when test="${user.avatarPath == 'null'}"></c:when>
                        <c:when test="${empty user.avatarPath}"></c:when>
                        <c:otherwise><img class="img-circle" src="../images/avatars/${user.avatarPath}" alt="Avatar" width="100" height="100"  border-radius="180px"></c:otherwise>
                    </c:choose>
                </c:if>
                <a class="navbar-brand text-light" style="font-size:30px;">${user.firstName} ${user.lastName}</a>
                <a href="<c:url context="${contextPath}" value="/restricted/edit.shopping.lists.html?id=${user.id}" />" class="fas fa-edit" style="font-size:20px;color:white" title="user settings" data-toggle="modal" data-target="#userDialog" data-user-id="${user.id}" data-user-email="${user.email} "data-user-name="${user.firstName}" data-user-lastname="${user.lastName}"></a>
                <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarSupportedContent" aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
                    <span class="navbar-toggler-icon"></span>
                </button>

                <div class="collapse navbar-collapse justify-content-end" id="navbarSupportedContent">
                    <ul class="navbar-nav">

                        <c:choose>
                            <c:when test="${sessionScope.user.type eq 'admin'}">
                                <li class="nav-item active">
                                    <a class="nav-link" href="users.html">Go to Users List <span class="sr-only"></span></a>
                                </li>
                            </c:when>
                            <c:otherwise>
                                <li class="nav-item active">
                                    <a class="nav-link" href="logout.handler">Logout <span class="sr-only"></span></a>
                                </li>
                            </c:otherwise>
                        </c:choose>
                    </ul>
                </div>
            </div>
        </nav>

        <!-- CARD LISTA -->
        <div class="container">
            <!-- DYNAMIC NOTIFICATIONS -->
            <c:if test="${deleted == 'true'}">
                <div class="alert alert-success" role="alert">
                    <center>Your shopping list was successfully deleted!</center>
                        <c:set var="deleted" value="false" scope="session"/>
                </div>
            </c:if>
            <c:if test="${shared == 'true'}">
                <div class="alert alert-success" role="alert">
                    <center> Your shopping list was successfully shared!</center>
                        <c:set var="shared" value="false" scope="session"/>
                </div>
            </c:if> 
            <c:if test="${notified == 'true'}">
                <div class="alert alert-success" role="alert">
                    <center> You succesfully sent a notification to other users!</center>
                    <c:set var="notified" value="false" scope="session"/>
                </div>
            </c:if> 
            <!-- ************************* -->   

            <div class="row">
                <div class="col-sm-8">
                    <div class="card">
                        <div class="card-header bg-primary text-white">
                            
                            <h5 class="mb-0">
                                <div class="card-title float-left">Shopping Lists</div>
                                <div class="float-right">
                                    <a href="<c:url context="${contextPath}" value="/restricted/edit.shopping.list.html?id=${shoppingList.id}" />" class="fas fa-plus" style="font-size:30px; color:white" title="add shopping list" data-toggle="modal" data-target="#editDialog" aria-hidden="true"></a>
                                    <a href="<c:url context="${contextPath}" value="/restricted/export2PDF?id=${user.id}" />" class="far fa-file-pdf" style="font-size:30px; color:white" aria-hidden="true"></a>   
                                </div>
                            </h5>                 
                            <br><br>
                                <form class="form" action="shopping.lists.handler" method="POST">     
                                    <div class="form-row">
                                        <div class="form-group col-md-5">
                                            <input type="hidden" name="operation" value="order">
                                          <label for="inputState">Order by</label>
                                          <select name="orderShoppingLists" id="orderSelect" onchange="this.form.submit()"  class="form-control">
                                            <option value="1" <c:if test="${order == 1}">selected</c:if>>Alphabetical (Ascending)</option>
                                            <option value="2" <c:if test="${order == 2}">selected</c:if>>Alphabetical (Descending)</option>
                                            <option value="3" <c:if test="${order == 3}">selected</c:if>>Category (Asending)</option>
                                            <option value="4" <c:if test="${order == 4}">selected</c:if>>Category (Descending)</option>
                                            <option value="5" <c:if test="${order == 5}">selected</c:if>>Creation Date (Asending)</option>
                                            <option value="6" <c:if test="${order == 6}">selected</c:if>>Creation Date (Descending)</option>
                                          </select>
                                        </div>  
                                    </div>
                                </form>
                             
                            </div>
                            <!-- Shopping Lists cards -->
                            <div id="accordion">
                            <c:choose>
                                <c:when test="${empty shoppingLists}">
                                    <div class="card">
                                        <div class="card-body">
                                            This collection is empty.
                                        </div>
                                    </div>
                                </c:when>
                                <c:otherwise>
                                    <c:set var="index" value="1" />
                                    <c:forEach var="shoppingList" items="${shoppingLists}">
                                        <div class="card">
                                            <div class="card-header" id="heading${index}">
                                                <h5 class="mb-0">
                                                    <button class="btn btn-link" data-toggle="collapse" data-target="#collapse${index}" aria-expanded="true" aria-controls="collapse${index}">
                                                        ${shoppingList.name} - (${shoppingList.category.name})
                                                    </button>
                                                    <div class="float-right">
                                                        
                                                        <a href="<c:url context="${contextPath}" value="/restricted/edit.shopping.list.html?id=${shoppingList.id}" />" class="fas fa-edit" style="font-size:15px;" title="edit &quot;${shoppingList.name}&quot; shopping list" data-toggle="modal" data-target="#editDialog" data-shopping-list-id="${shoppingList.id}" data-shopping-list-name="${shoppingList.name}" data-shopping-list-description="${shoppingList.description}"></a>
                                                        <a href="<c:url context="${contextPath}" value="/restricted/notify.shopping.list.html?id=${shoppingList.id}" />" class="fas fa-bell" style="font-size:15px;" title="notify &quot;${shoppingList.name}&quot; users" data-toggle="modal" data-target="#notifyDialog" data-shopping-list-id="${shoppingList.id}"></a>
                                
                                                        <a href="<c:url context="${contextPath}" value="/restricted/edit.shopping.list.html?id=${shoppingList.id}" />" class="fas fa-share-alt" style="font-size:15px;" title="share &quot;${shoppingList.name}&quot; shopping list" data-toggle="modal" data-target="#shareDialog" data-shopping-list-id="${shoppingList.id}" data-shopping-list-name="${shoppingList.name}" data-shopping-list-description="${shoppingList.description}"></a>
                                                        <a href="<c:url context="${contextPath}" value="/restricted/edit.shopping.list.html?id=${shoppingList.id}" />" class="fas fa-trash-alt" style="font-size:15px;" title="delete &quot;${shoppingList.name}&quot; shopping list" data-toggle="modal" data-target="#deleteDialog" data-shopping-list-id="${shoppingList.id}" data-shopping-list-name="${shoppingList.name}" data-shopping-list-description="${shoppingList.description}"></a>
                                                        <a href="https://www.google.it/maps/search/${shoppingList.category.name}+vicino+a+me" class="fas fa-map" target=”_blank” style="font-size:15px;" title="${shoppingList.category.name} near me"></a>
                                                    </div>
                                                </h5>
                                            </div>
                                            <div id="collapse${index}" class="collapse${index eq 1 ? " show" : ""}" aria-labelledby="heading${index}" data-parent="#accordion">
                                                <div class="card-body ">
                                                    <center> Description : ${shoppingList.description}</center>
                                                    <HR width="350" size="1" color="lightblue" align="center">
                                                    <c:if test="${not empty shoppingList.getProducts()}">
                                                        <!-- PRODUCTS per LIST --> 
                                                        <c:forEach var="product" items="${shoppingList.getProducts()}">
                                                            <div>
                                                                <ul>
                                                                    <li>${product.name} [Quantity: ${product.getQuantity()}]
                                                                        <!-- BOTTONE X ROSSA DI ESEMPIO -->    
                                                                        <a href="<c:url context="${contextPath}" value="/restricted/delete.product.html?id=${product.getId()}" />" class="fas fa-window-close red-window-close" style="font-size:15px;" title="delete &quot;${product.getName()}&quot; product" data-toggle="modal" data-target="#deleteProduct" data-shopping-list-id="${shoppingList.id}" data-product-id="${product.getId()}"></a>

                                                                        
                                                                        <!-- *************************************************************  -->
                                                                    </li>
                                                                </ul>
                                                            </div>
                                                        </c:forEach>
                                                    </c:if>
                                                    <c:if test="${shoppingList.category.id != 1}">
                                                        <center>
                                                            <div>
                                                                <a href="<c:url context="${contextPath}" value="/restricted/add_product.html?id=${shoppingList.id}" />" class="fas fa-plus" style="font-size:20px; "title="add product to &quot;${shoppingList.name}&quot;" data-toggle="modal" data-target="#addProduct" data-shopping-list-id="${shoppingList.id}" data-shopping-list-name="${shoppingList.name}" data-shopping-list-description="${shoppingList.description}" data-shopping-list-category-id="${shoppingList.category.id}"></a>
                                                            </div>
                                                        </center>
                                                    </c:if>
                                                </div>
                                            </div>
                                            <c:set var="index" value="${index + 1}" />
                                        </div>
                                    </c:forEach>
                                </c:otherwise>
                            </c:choose>
                        </div>                    
                    </div>
                </div>
                <div class="col-sm-4 ">
                    <div class="card">
                        <div class="card-header bg-success text-white">
                            <h5 class="mb-0">
                                Notifications
                                
                            </h5>
                        </div>
                        <div class="card-body">
                            
                             <c:choose>
                                <c:when test="${not empty notifications}">
                                    <ul>
                                        <c:forEach var="notification" items="${notifications}">
                                            <li>
                                                <c:choose>
                                                    <c:when test="${notification.getMessageId() == 1}">
                                                        ${notification.getShoppingList().getName()} was updated!
                                                    </c:when>
                                                    <c:when test="${notification.getMessageId() == 2}">
                                                        ${notification.getShoppingList().getName()} has some new products!
                                                    </c:when>
                                                    <c:when test="${notification.getMessageId() == 3}">
                                                        Some products were deleted from ${notification.getShoppingList().getName()} !
                                                    </c:when>
                                                    <c:otherwise></c:otherwise>
                                                </c:choose>
                                                <form class="float-right" action="shopping.lists.handler" method="POST">
                                                    <input type="hidden" name="operation" value="deleteNotification">
                                                    <input type="hidden" name="idUser" value="${user.id}">
                                                    <input type="hidden" name="deleteMessageId" value="${notification.getMessageId()}">
                                                    <input type="hidden" name="deleteShoppingListId" value="${notification.getShoppingList().getId()}">
                                                    <a href="#" class="fas fa-window-close red-window-close" title="delete notification" onclick="$(this).closest('form').submit()"></a>
                                                </form>
                                            </li>
                                        </c:forEach>
                                    </ul>
                                </c:when>
                                <c:otherwise>You have no new notifications!</c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                </div> 
                <!--
                    <div class="col-sm-3 col-sm-offset-1">
                        <div class="card">
                          <div class="card-body">
                            <h5 class="card-title">Chat</h5>

                          </div>
                        </div>
                    </div>        
                -->
            </div>

            <footer>
                <small>Copyright &copy; 2018</small>
            </footer>
        </div>

        <!-- USER DIALOG -->
        <form action="<c:url context="${contextPath}" value="/restricted/user.handler" />" method="POST" enctype="multipart/form-data">
            <div class="modal fade" id="userDialog" tabindex="-1" role="dialog" aria-labelledby="titleLabel4" enctype="multipart/form-data">
                <div class="modal-dialog modal-dialog-centered" role="document">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h3 class="modal-title" id="titleLabel2">Edit Account: </h3>
                            <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true"><i class="fas fa-window-close red-window-close"></i></span></button>
                        </div>
                        <div class="modal-body">
                            <input type="hidden" name="idUser" value="${user.id}">
                            <div class="form-label-group">
                                <input type="text" id="uMail" name="uMail" class="form-control" placeholder="Email" value="${user.email}" readonly>
                                <label for="uMail">Email</label>
                            </div>
                            <div class="form-label-group">
                                <input type="text" id="firstname" name="firstname" class="form-control" placeholder="Name" value="${user.firstName}" readonly>
                                <label for="firstname">Name</label>
                            </div>
                            <div class="form-label-group">
                                <input type="text" id="lastname" name="lastname" class="form-control" placeholder="Last Name" value="${user.lastName}" readonly>
                                <label for="lastname">Last Name</label>
                            </div>
                            <div class="form-label-group">
                                <input type="file" id="avatar" name="avatar" class="form-control" placeholder="Avatar">
                                <label for="avatar">Avatar</label>
                            </div>
                        </div>
                        <div class="modal-footer">
                            <button type="submit" class="btn btn-primary" id="deleteDialogSubmit">Save</button>
                            <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
                        </div>
                    </div>
                </div>
            </div>
        </form>

        <!-- DELETE DIALOG -->
        <form action="shopping.lists.handler" method="POST">
            <div class="modal fade" id="deleteDialog" tabindex="-1" role="dialog" aria-labelledby="titleLabel3">
                <div class="modal-dialog modal-dialog-centered" role="document">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h3 class="modal-title" id="titleLabel2">Delete Shopping List: </h3>
                            <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true"><i class="fas fa-window-close red-window-close"></i></span></button>
                        </div>
                        <div class="modal-body">
                            <input type="hidden" name="idUser" value="${user.id}">
                            <input type="hidden" name="deleteId" id="deleteId">
                            <input type="hidden" name="operation" value="delete">
                            <div class="form-label-group">
                                Are you really sure you want to delete shopping list?
                            </div>
                        </div>
                        <div class="modal-footer">
                            <button type="submit" class="btn btn-primary" id="deleteDialogSubmit">Delete</button>
                            <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
                        </div>
                    </div>
                </div>
            </div>
        </form>

        <!-- DELETE PRODUCT -->
        <form action="shopping.lists.handler" method="POST">
            <div class="modal fade" id="deleteProduct" tabindex="-1" role="dialog" aria-labelledby="titleLabel3">
                <div class="modal-dialog modal-dialog-centered" role="document">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h3 class="modal-title" id="titleLabel2">Delete Product:</h3>
                            <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true"><i class="fas fa-window-close red-window-close"></i></span></button>
                        </div>
                        <div class="modal-body">
                            <input type="hidden" name="deleteProductShoppingListId" id="deleteProductShoppingListId">
                            <input type="hidden" name="deleteProductId" id="deleteProductId">
                            <input type="hidden" name="operation" value="deleteProduct">
                            <div class="form-label-group">
                                Are you really sure you want to delete this product?
                            </div>
                        </div>
                        <div class="modal-footer">
                            <button type="submit" class="btn btn-primary" id="deleteDialogSubmit">Delete</button>
                            <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
                        </div>
                    </div>
                </div>
            </div>
        </form>
        
        <!-- NOTIFY DIALOG -->
        <form action="shopping.lists.handler" method="POST">
            <div class="modal fade" id="notifyDialog" tabindex="-1" role="dialog" aria-labelledby="titleLabel2">
                <div class="modal-dialog modal-dialog-centered" role="document">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h3 class="modal-title" id="titleLabel2">Notify other users: </h3>
                            <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true"><i class="fas fa-window-close red-window-close"></i></span></button>
                        </div>
                        <div class="modal-body">
                            <input type="hidden" name="idUser" value="${user.id}">
                            <input type="hidden" name="notifyShoppingListId" id="notifyShoppingListId">
                            <input type="hidden" name="operation" value="notify">
                            <select id="notifyMessage" name="notifyMessage" class="form-control">
                                <option value="1">I've updated this shopping list! Check it out!</option>
                                <option value="2">I've added something to this shopping list! Check it out!</option>
                                <option value="3">I've deleted something from this shopping list! Check it out!</option>
                            </select>
                        </div>
                        <div class="modal-footer">
                            <button type="submit" class="btn btn-primary" id="shareDialogSubmit">Notify</button>
                            <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
                        </div>
                    </div>
                </div>
            </div>
        </form>
        
        <!-- SHARE DIALOG -->
        <form action="shopping.lists.handler" method="POST">
            <div class="modal fade" id="shareDialog" tabindex="-1" role="dialog" aria-labelledby="titleLabel2">
                <div class="modal-dialog modal-dialog-centered" role="document">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h3 class="modal-title" id="titleLabel2">Share Shopping List: </h3>
                            <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true"><i class="fas fa-window-close red-window-close"></i></span></button>
                        </div>
                        <div class="modal-body">
                            <input type="hidden" name="idUser" value="${user.id}">
                            <input type="hidden" name="shareId" id="shareId">
                            <input type="hidden" name="operation" value="share">
                            <div class="form-label-group">
                                <input type="text" name="email" id="email" class="form-control" placeholder="Email" required autofocus>
                                <label for="email">Email</label>
                            </div>
                        </div>
                        <div class="modal-footer">
                            <button type="submit" class="btn btn-primary" id="shareDialogSubmit">Share</button>
                            <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
                        </div>
                    </div>
                </div>
            </div>
        </form>

        <!-- CREATE/EDIT DIALOG -->
        <form action="shopping.lists.handler" method="POST">
            <div class="modal fade" id="editDialog" tabindex="-1" role="dialog" aria-labelledby="titleLabel">
                <div class="modal-dialog modal-dialog-centered" role="document">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h3 class="modal-title" id="titleLabel">Create new/Edit Shopping List</h3>
                            <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true"><i class="fas fa-window-close red-window-close"></i></span></button>
                        </div>
                        <div class="modal-body">
                            <input type="hidden" name="idUser" value="${user.id}">
                            <input type="hidden" name="idShoppingList" id="idShoppingList">
                            <input type="hidden" name="operation" value="cred">
                            <div class="form-label-group">
                                <input type="text" name="name" id="name" class="form-control" placeholder="Name" required autofocus>
                                <label for="name">Name</label>
                            </div>
                            <div class="form-label-group">
                                <input type="text" name="description" id="description" class="form-control" placeholder="Description" required>
                                <label for="description">Description</label>
                            </div>
                            <div class="form-group" id="categorySelect">
                                <label for="idCategory">Category</label>
                                <select class="form-control" id="idCategory" name="idCategory" >
                                    <c:forEach var="category" items="${categories}">
                                        <option value="${category.id}">${category.name}</option>
                                    </c:forEach>
                                </select>
                            </div>
                        </div>
                        <div class="modal-footer">
                            <button type="submit" class="btn btn-primary" id="editDialogSubmit">Create</button>
                            <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
                        </div>
                    </div>
                </div>
            </div>
        </form>

        <!-- ADD PROUCT DIALOG -->
        <form action="shopping.lists.handler" method="POST">
            <div class="modal fade" id="addProduct" tabindex="-1" role="dialog" aria-labelledby="titleLabel">
                <div class="modal-dialog modal-dialog-centered" role="document">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h3 class="modal-title" id="titleLabel">Add Product</h3>
                            <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true"><i class="fas fa-window-close red-window-close"></i></span></button>
                        </div>
                        <div class="modal-body">
                            <div class="form-row" id="divProductSelect">
                            <input type="hidden" name="productShoppingListId" id="productShoppingListId">
                            <input type="hidden" name="productCategoryId" id="productCategoryId">
                            <input type="hidden" name="operation" value="addProduct">
                            
                            
                                <div class="form-group col-md-5">
                                  <label for="productSelect${category.id}">Products</label>
                                        <c:forEach var="category" items="${categories}">
                                          <select class="form-control" id="productSelect${category.id}" name="productSelect${category.id}" hidden>
                                              <c:forEach var="product" items="${products}">
                                                  <c:if test="${product.category.id == category.id}">
                                                      <option value="${product.id}">${product.name}</option>
                                                  </c:if>
                                              </c:forEach>
                                          </select>
                                        </c:forEach>
                                </div>
                                <div class="form-group col-md-2">
                                  <label for="quantity">Quantity</label>
                                  <input type="number" name="quantity" min="0" max="100" value="1" class="form-control" id="quantity" required>
                                </div>
                            </div>
                            
                            
                        </div>
                        <div class="modal-footer">
                            <button type="submit" class="btn btn-primary" id="editDialogSubmit">Add</button>
                            <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
                        </div>
                    </div>
                </div>
            </div>
        </form>
        <!-- Latest compiled and minified JavaScript -->
        <script src="https://code.jquery.com/jquery-3.2.1.min.js" crossorigin="anonymous"></script>
        <script src="https://cdnjs.cloudflare.com/ajax/libs/moment.js/2.18.1/moment.min.js" crossorigin="anonymous"></script>
        <script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/js/bootstrap.min.js" crossorigin="anonymous"></script>
        <script type="text/javascript">
            $(function () {
                $("#editDialog").on("show.bs.modal", function (e) {
                    var target = $(e.relatedTarget);
                    var shoppingListId = target.data("shopping-list-id");
                    if (shoppingListId !== undefined) {
                        var shoppingListName = target.data("shopping-list-name");
                        var shoppingListDescription = target.data("shopping-list-description");

                        $("#titleLabel").html("Edit Shopping List:");
                        $("#editDialogSubmit").html("Update");
                        document.getElementById('categorySelect').hidden = true;
                        $("#idShoppingList").val(shoppingListId);
                        $("#name").val(shoppingListName);
                        $("#description").val(shoppingListDescription);
                    } else {
                        $("#titleLabel").html("Create a new Shopping List:");
                        $("#editDialogSubmit").html("Create");
                        document.getElementById('categorySelect').hidden = false;
                        $("#idShoppingList").val(null);
                        $("#name").val(null);
                        $("#description").val(null);
                    }
                });

                $("#shareDialog").on("show.bs.modal", function (e) {
                    var target = $(e.relatedTarget);
                    var shoppingListId = target.data("shopping-list-id");
                    $("#shareId").val(shoppingListId);
                });

                $("#deleteDialog").on("show.bs.modal", function (e) {
                    var target = $(e.relatedTarget);
                    var shoppingListId = target.data("shopping-list-id");
                    $("#deleteId").val(shoppingListId);
                });

                $("#deleteProduct").on("show.bs.modal", function (e) {
                    var target = $(e.relatedTarget);
                    var shoppingListId = target.data("shopping-list-id");
                    var productId = target.data("product-id");
                    $("#deleteProductShoppingListId").val(shoppingListId);
                    $("#deleteProductId").val(productId);
                });
                
                $("#notifyDialog").on("show.bs.modal", function (e) {
                    var target = $(e.relatedTarget);
                    var shoppingListId = target.data("shopping-list-id");
                    $("#notifyShoppingListId").val(shoppingListId);
                });

                $("#addProduct").on("show.bs.modal", function (e) {
                    var target = $(e.relatedTarget);
                    var shoppingListId = target.data("shopping-list-id");
                    var categoryId = target.data("shopping-list-category-id");
                    $("#productShoppingListId").val(shoppingListId);
                    $("#productCategoryId").val(categoryId);
                    var productSelect = document.getElementsByClassName("productSelect");
                    var i;
                    for (i = 0; i < productSelect.length; i++)
                        productSelect[i].hidden = true;

                    document.getElementById('productSelect' + categoryId).hidden = false;
                });
            });
        </script>
    </body>
</html>