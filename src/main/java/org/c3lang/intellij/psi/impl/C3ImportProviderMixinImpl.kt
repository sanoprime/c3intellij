package org.c3lang.intellij.psi.impl

import com.intellij.lang.ASTNode
import com.intellij.psi.util.childrenOfType
import com.intellij.psi.util.parentOfType
import org.c3lang.intellij.index.NameIndexService
import org.c3lang.intellij.psi.*

abstract class C3ImportProviderMixinImpl(node: ASTNode) : C3PsiElementImpl(node), C3ImportProvider {

    override val imports: List<ModuleName>
        get() = ModuleName.getImportList(this)
    override val importDeclarations: List<C3ImportDecl>
        get() = childrenOfType<C3TopLevel>().mapNotNull { it.importDecl }
    override val moduleName: ModuleName?
        get() = ModuleName.from(this)
    override val importPaths: List<C3ImportPath>
        get() = importDeclarations.flatMap { it.importPaths.importPathList }

    override fun contains(
        pathIdent: C3PathIdent
    ): Boolean {
        val elements = getImportOf(pathIdent)

        return elements.isNotEmpty()
    }

    override fun contains(path: C3Path): Boolean = path.parentOfType<C3PathIdent>()?.let { contains(it) } == true

    override fun getImportOf(pathIdent: C3PathIdent): List<C3ImportPath> = getImportOf(pathIdent.text)

    override fun getImportOf(pathIdentExpr: C3PathIdentExpr): List<C3ImportPath> = getImportOf(pathIdentExpr.text)

    private fun getImportOf(text: String): List<C3ImportPath> {
        val elements = NameIndexService.findByNameEndsWith(text, project).filter {
            it.fqName.suffixName == text
        }.mapNotNull { it.moduleName?.value }

        return importPaths.filter {
            elements.contains(it.moduleName?.value)
        }
    }


    override fun getImportPaths(moduleName: ModuleName): List<C3ImportPath> {
        return importPaths.filter { it.moduleName?.value == moduleName.value }
    }
}