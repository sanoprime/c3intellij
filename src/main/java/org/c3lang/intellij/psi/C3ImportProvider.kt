package org.c3lang.intellij.psi

interface C3ImportProvider : C3ModuleNamePsiElement {
    val imports: List<ModuleName>
    val importDeclarations: List<C3ImportDecl>
    val importPaths: List<C3ImportPath>

    fun contains(pathIdent: C3PathIdent): Boolean
    fun contains(path: C3Path): Boolean
    fun getImportOf(pathIdent: C3PathIdent) : List<C3ImportPath>
    fun getImportOf(pathIdent: C3PathIdentExpr) : List<C3ImportPath>
    fun getImportPaths(moduleName: ModuleName): List<C3ImportPath>
}