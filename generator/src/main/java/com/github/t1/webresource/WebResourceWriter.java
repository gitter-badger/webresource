package com.github.t1.webresource;

class WebResourceWriter {
    private final StringBuilder out = new StringBuilder();

    private final String pkg;
    private final String simple;
    private final String lower;

    private int indent = 0;

    public WebResourceWriter(String pkg, String simple) {
        this.pkg = pkg;
        this.simple = simple;
        this.lower = simple.toLowerCase();
    }

    public String run() {
        append("package " + pkg + ";");
        nl();
        imports();
        nl();
        classDeclaration();
        ++indent;
        entityManager();
        nl();
        getAll();
        nl();
        getOne();
        nl();
        post();
        nl();
        put();
        nl();
        delete();
        --indent;
        append("}");

        return out.toString();
    }

    private void imports() {
        append("import java.util.List;");
        append("import javax.ejb.Stateless;");
        append("import javax.persistence.*;");
        append("import javax.ws.rs.*;");
        append("import javax.ws.rs.core.*;");
        append("import javax.ws.rs.core.Response.Status;");
    }

    private void classDeclaration() {
        append("@Path(\"/\")");
        append("@Stateless");
        append("public class " + simple + "WebResource {");
    }

    private void entityManager() {
        append("@PersistenceContext");
        append("private EntityManager em;");
    }

    private void getAll() {
        append("@GET");
        append("@Path(\"" + lower + "s\")");
        append("public List<" + simple + "> get" + simple + "s() {");
        ++indent;
        append("return em.createQuery(\"FROM " + simple + " ORDER BY id\", " + simple + ".class).getResultList();");
        --indent;
        append("}");
    }

    private void getOne() {
        append("@GET");
        append("@Path(\"" + lower + "/{id}\")");
        append("public Response get" + simple + "(@PathParam(\"id\") long id) {");
        ++indent;
        append(simple + " result = em.find(" + simple + ".class, id);");
        append("if (result == null) {");
        ++indent;
        append("return Response.status(Status.NOT_FOUND).build();");
        --indent;
        append("}");
        append("return Response.ok(result).build();");
        --indent;
        append("}");
    }

    private void post() {
        append("@POST");
        append("@Path(\"" + lower + "s\")");
        append("public Response create" + simple + "(" + simple + " " + lower + ", @Context UriInfo uriInfo) {");
        ++indent;
        append("em.persist(" + lower + ");");
        append("em.flush();");
        nl();
        append("UriBuilder builder = uriInfo.getBaseUriBuilder();");
        append("builder.path(\"" + lower + "\").path(Long.toString(" + lower + ".getId()));");
        append("return Response.created(builder.build()).build();");
        --indent;
        append("}");
    }

    private void put() {
        append("@PUT");
        append("@Path(\"" + lower + "/{id}\")");
        append("public Response update" + simple + "(@PathParam(\"id\") long id, " + simple + " " + lower + ") {");
        ++indent;
        append("if (" + lower + ".getId() == null) {");
        ++indent;
        append(lower + ".setId(id);");
        --indent;
        append("} else if (id != " + lower + ".getId()) {");
        ++indent;
        append("String message = \"id conflict! path=\" + id + \", body=\" + " + lower + ".getId() + \".\\n\"");
        append("    + \"either leave the id in the body null or set it to the same id\";");
        append("return Response.status(Status.BAD_REQUEST).entity(message).build();");
        --indent;
        append("}");
        append(simple + " result = em.merge(" + lower + ");");
        append("if (result == null) {");
        ++indent;
        append("return Response.status(Status.NOT_FOUND).build();");
        --indent;
        append("}");
        append("return Response.ok(result).build();");
        --indent;
        append("}");
    }

    private void delete() {
        append("@DELETE");
        append("@Path(\"" + lower + "/{id}\")");
        append("public Response delete" + simple + "(@PathParam(\"id\") long id) {");
        ++indent;
        append(simple + " result = em.find(" + simple + ".class, id);");
        append("if (result == null) {");
        ++indent;
        append("return Response.status(Status.NOT_FOUND).build();");
        --indent;
        append("}");
        append("em.remove(result);");
        append("return Response.ok(result).build();");
        --indent;
        append("}");
    }

    private void append(String string) {
        appendIndent();
        out.append(string);
        nl();
    }

    private void appendIndent() {
        for (int i = 0; i < indent; i++) {
            out.append("    ");
        }
    }

    private void nl() {
        out.append('\n');
    }
}
