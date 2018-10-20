<%@page contentType="text/html" pageEncoding="UTF-8" errorPage="ooops.jsp"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
        <title>Admin Page: Categories</title>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
        <!-- Latest compiled and minified CSS -->
        <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/css/bootstrap.min.css" crossorigin="anonymous">
        <link rel="stylesheet" href="https://use.fontawesome.com/releases/v5.0.8/css/all.css" crossorigin="anonymous">
        <link rel="stylesheet" href="../css/floating-labels.css">
        <link rel="stylesheet" href="../css/forms.css">
        <style>
            body {
                padding-top: 100px;
            }
        </style>
    </head>
    <body>
        <nav class="navbar fixed-top navbar-expand-lg navbar-dark bg-dark "> 
            <div class="container">

                <ul class="nav">
                    <li class="nav-item">
                        <a class="nav-link" href="<c:url context="${contextPath}" value="/restricted/products.html"/>">Products</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link active" href="<c:url context="${contextPath}" value="/restricted/categories.html"/>">Categories</a>
                    </li>
                </ul>


                <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarSupportedContent" aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
                    <span class="navbar-toggler-icon"></span>
                </button>

                <div class="collapse navbar-collapse justify-content-end" id="navbarSupportedContent">
                    <ul class="navbar-nav">
                        <li class="nav-item active">
                            <a class="nav-link" href="logout.handler">Logout <span class="sr-only"></span></a>
                        </li>

                    </ul>
                </div>
            </div>
        </nav>


        <div class="container">
            <div class="card ">
                <div class="card-header bg-primary text-white">
                    <h5 class="card-title float-left">Categories</h5>
                    <a href="<c:url context="${contextPath}" value="/restricted/edit.shopping.list.html?id=${shoppingList.id}" />" class="fas fa-plus float-right" style="font-size:30px; color:white" title="add shopping list" data-toggle="modal" data-target="#editDialog" aria-hidden="true"></a>                </div>

                <!-- Shopping Lists cards -->
                <div id="accordion">
                    <c:choose>
                        <c:when test="${empty categories}">
                            <div class="card">
                                <div class="card-body">
                                    This collection is empty.
                                </div>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <c:set var="index" value="1" />

                            <c:forEach var="category" items="${categories}">
                                <div class="card text-black bg-light">
                                    <div class="card-body" id="heading${index}">

                                        <h5 class="card-title">
                                            <button class="btn btn-link" data-toggle="collapse" data-target="#collapse${index}" aria-expanded="true" aria-controls="collapse${index}">
                                                ${category.name}
                                            </button>
                                            <div class="float-right">
                                                <a href="<c:url context="${contextPath}" value="/restricted/edit.categories.html?id=${category.id}" />" class="fas fa-edit" style="font-size:15px;" title="edit &quot;${category.name}&quot; category" data-toggle="modal" data-target="#editDialog" data-category-id="${category.id}" data-category-name="${category.name}" data-category-description="${category.description}"></a>
                                                <a href="<c:url context="${contextPath}" value="/restricted/delete.categories.html?id=${category.id}" />" class="fas fa-trash-alt" style="font-size:15px;" title="delete &quot;${category.name}&quot; category" data-toggle="modal" data-target="#deleteDialog" data-category-id="${category.id}" data-category-name="${category.name}" data-category-description="${category.description}"></a>

                                            </div>
                                        </h5>

                                        <div id="collapse${index}" class="collapse${index eq 1 ? " show" : ""}" aria-labelledby="heading${index}" data-parent="#accordion">
                                            <div class="card-body" style="font-size:17px;color:black;font-family: sans-serif;">
                                                ${category.description}
                                            </div>
                                        </div>

                                    </div>
                                    <c:set var="index" value="${index + 1}" />
                                </div>
                            </c:forEach>

                        </c:otherwise>
                    </c:choose>
                </div>                    
            </div>

            <footer>
                <small>Copyright &copy; 2018</small>
            </footer>
        </div>            

        <!-- DELETE DIALOG -->
        <form action="categories.handler" method="POST">
            <div class="modal fade" id="deleteDialog" tabindex="-1" role="dialog" aria-labelledby="titleLabel3">
                <div class="modal-dialog modal-dialog-centered" role="document">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h3 class="modal-title" id="titleLabel2">Delete Category:</h3>
                            <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true"><i class="fas fa-window-close red-window-close"></i></span></button>
                        </div>
                        <div class="modal-body">
                            <input type="hidden" name="deleteId" id="deleteId">
                            <input type="hidden" name="operation" value="delete">
                            <div class="form-label-group">
                                Are you really sure you want to delete this category?
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

        <!-- CREATE/EDIT DIALOG -->
        <form action="categories.handler" method="POST">
            <div class="modal fade" id="editDialog" tabindex="-1" role="dialog" aria-labelledby="titleLabel">
                <div class="modal-dialog modal-dialog-centered" role="document">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h3 class="modal-title" id="titleLabel">Add new/Edit Category</h3>
                            <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true"><i class="fas fa-window-close red-window-close"></i></span></button>
                        </div>
                        <div class="modal-body">
                            <input type="hidden" name="idCategory" id="idCategory">
                            <input type="hidden" name="operation" value="cred">
                            <div class="form-label-group">
                                <input type="text" name="name" id="name" class="form-control" placeholder="Name" required autofocus>
                                <label for="name">Name</label>
                            </div>
                            <div class="form-label-group">
                                <input type="text" name="description" id="description" class="form-control" placeholder="Description" required>
                                <label for="description">Description</label>
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

        <!-- Latest compiled and minified JavaScript -->
        <script src="https://code.jquery.com/jquery-3.2.1.min.js" crossorigin="anonymous"></script>
        <script src="https://cdnjs.cloudflare.com/ajax/libs/moment.js/2.18.1/moment.min.js" crossorigin="anonymous"></script>
        <script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/js/bootstrap.min.js" crossorigin="anonymous"></script>
        <script type="text/javascript">
            $(function () {
                $("#editDialog").on("show.bs.modal", function (e) {
                    var target = $(e.relatedTarget);
                    var categoryId = target.data("category-id");
                    if (categoryId !== undefined) {
                        var categoryName = target.data("category-name");
                        var categoryDescription = target.data("category-description");

                        $("#titleLabel").html("Edit Category:");
                        $("#editDialogSubmit").html("Update");
                        $("#idCategory").val(categoryId);
                        $("#name").val(categoryName);
                        $("#description").val(categoryDescription);
                    } else {
                        $("#titleLabel").html("Add a new Category!");
                        $("#editDialogSubmit").html("Add");
                        $("#idCategory").val(null);
                        $("#name").val(null);
                        $("#description").val(null);
                    }
                });

                $("#deleteDialog").on("show.bs.modal", function (e) {
                    var target = $(e.relatedTarget);
                    var categoryId = target.data("category-id");
                    $("#deleteId").val(categoryId);
                });
            });
        </script>
    </body>
</html>