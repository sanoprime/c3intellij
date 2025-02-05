package org.c3lang.intellij.index

import com.intellij.openapi.project.Project
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.stubs.StubIndex
import org.c3lang.intellij.psi.C3CallablePsiElement
import org.c3lang.intellij.psi.C3FullyQualifiedNamePsiElement
import org.c3lang.intellij.psi.C3ImportPath
import org.c3lang.intellij.psi.C3PsiElement

object NameIndexService {
    fun findByNameEndsWith(name: String, project: Project): Collection<C3FullyQualifiedNamePsiElement> {
        return StubIndex.getInstance().getAllKeys(NameIndex.KEY, project).filter { it.endsWith(name) }.flatMap {
            getElementsByName(it, project)
        }.filterIsInstance<C3FullyQualifiedNamePsiElement>()
    }

    fun findByNameEndsWith(
        path: C3ImportPath,
        callName: String
    ): Collection<C3CallablePsiElement> {
        val name = "${path.text}::$callName"

        return StubIndex.getInstance().getAllKeys(NameIndex.KEY, path.project).filter { it.endsWith(name) }.flatMap {
            getElementsByName(it, path.project)
        }.filterIsInstance<C3CallablePsiElement>()
    }

    private fun getElementsByName(
        string: String,
        project: Project
    ): Collection<C3PsiElement?> = StubIndex.getElements(
        NameIndex.KEY,
        string,
        project,
        GlobalSearchScope.allScope(project),
        C3PsiElement::class.java
    )
}